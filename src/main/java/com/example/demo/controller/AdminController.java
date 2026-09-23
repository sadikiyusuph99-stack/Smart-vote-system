package com.example.demo.controller;

import com.example.demo.model.candidate;
import com.example.demo.repository.CandidateRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.websocket.Encoder.Text;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CandidateRepository candidateRepository;

    // Folda ambapo picha zitahifadhiwa ndani ya seva yako
    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
private String UPLOAD_DIR;

    AdminController(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("allCandidatesList", candidateRepository.findAll());
        model.addAttribute("presidentCount", candidateRepository.findByPositionType("President").size());
        model.addAttribute("collegeMpCount", candidateRepository.findByPositionType("College_MP").size());
        model.addAttribute("deptMpCount", candidateRepository.findByPositionType("Department_MP").size());
        return "admin_dashboard";
    }

    @PostMapping("/addCandidate")
    public String addCandidate(@RequestParam("position_type") String positionType,
                               @RequestParam(value = "mainName", required = false) String mainName,
                               @RequestParam(value = "viceName", required = false) String viceName,
                               @RequestParam(value = "college", required = false) Long collegeId,
                               @RequestParam(value = "department", required = false) Long departmentId,
                               @RequestParam(value = "main_photo", required = false) MultipartFile mainPhoto,
                               @RequestParam(value = "vice_photo", required = false) MultipartFile vicePhoto,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
                               Model model) {

        // Kikomo cha wagombea 3: kwa Rais ni jumla (global), kwa MP ni KWA KILA CHUO/IDARA (siyo jumla nzima)
        long existingCount;
        if ("President".equals(positionType)) {
            existingCount = candidateRepository.findByPositionType(positionType).size();
        } else if ("College_MP".equals(positionType)) {
            existingCount = candidateRepository.findByPositionTypeAndCollegeId(positionType, collegeId).size();
        } else { // Department_MP
            existingCount = candidateRepository.findByPositionTypeAndDepartmentId(positionType, departmentId).size();
        }
        if (existingCount >= 3) {
            String scopeMsg = "President".equals(positionType) ? "" : " kwa chuo/idara hii";
            redirectAttributes.addFlashAttribute("uploadError",
                    "Kikomo cha wagombea 3 kwa nafasi hii ('" + positionType + "')" + scopeMsg + " kimeshafikiwa.");
            return "redirect:/admin/dashboard";
        }

        try {
            candidate candidate = new candidate();
            candidate.setPositionType(positionType);
            System.out.println("---Form imefika kwenye post method---");

            if ("President".equals(positionType)) {
                candidate.setFullName(mainName + "&" + viceName);

                // Hifadhi picha ya Rais
                if (mainPhoto != null && !mainPhoto.isEmpty()) {
                    String fileName = saveFile(mainPhoto);
                    candidate.setPhotoPath(fileName);
                }
                // Hifadhi picha ya Vice President kwenye field yake tofauti
                if (vicePhoto != null && !vicePhoto.isEmpty()) {
                    String fileName = saveFile(vicePhoto);
                    candidate.setVicePhotoPath(fileName);
                }
            } else {
                // Ni Mbunge wa Chuo au Idara
                candidate.setFullName(mainName);
                candidate.setCollegeId(collegeId);
                if ("Department_MP".equals(positionType)) {
                    candidate.setDepartmentId(departmentId);
                }

                // Hifadhi picha ya Mbunge
                if (mainPhoto != null && !mainPhoto.isEmpty()) {
                    String fileName = saveFile(mainPhoto);
                    candidate.setPhotoPath(fileName);
                }
            }

            candidateRepository.save(candidate);

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("uploadError", "Failed to upload candidate photos.");
            return "redirect:/admin/dashboard";
        }

        return "redirect:/admin/dashboard?success";
    }

    // Mbinu msaidizi ya kuhifadhi faili la picha na kulipa jina la kipekee (UUID)
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tengeneza jina la kipekee (mfano: 3f82cd1a-photo.jpg) ili picha zisifanane majina
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }
    @GetMapping("/results")
    public String showElectionResults(org.springframework.ui.Model model){

        java.util.List<java.util.Map<String, Object>>results =candidateRepository.getElectionResults();

        model.addAttribute("resultsList",results);
        return "result";
    }
    @GetMapping("/results/pdf")
public void downloadResultsPDF(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=MUSTSO_ELECTION_RESULTS_2026.pdf");

    java.util.List<java.util.Map<String, Object>> results = candidateRepository.getElectionResults();
   com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
   com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());

   document.open();

   com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("MUSTSO ELECTION RESULTS 2026");
    title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
    document.add(title);
    document.add(new com.lowagie.text.Paragraph("\n")); // Piga mstari wa mapumziko
    // Tengeneza Jedwali la PDF
    com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(3);
    table.setWidthPercentage(100);

    table.addCell("Candidate Name / Team");
    table.addCell("Position");
    table.addCell("Total Votes");

    for (java.util.Map<String, Object> row : results) {
        table.addCell(row.get("name").toString());
        table.addCell(row.get("position").toString());
        table.addCell(row.get("total_votes").toString());
    }

    document.add(table);
    document.close();

    }

}