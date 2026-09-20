package com.example.demo.controller;

import com.example.demo.model.validStudent;
import com.example.demo.model.Voter;
import com.example.demo.repository.validStudentRepository;
import com.example.demo.repository.VoterRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
public class AuthController {

    private final validStudentRepository validStudentRepository;
    private final VoterRepository voterRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthController(VoterRepository voterRepository, validStudentRepository validStudentRepository) {
        this.voterRepository = voterRepository;
        this.validStudentRepository = validStudentRepository;
    }

    // 1. Onyesha ukurasa wa Sign-Up
    @GetMapping("/signup")
    public String showSignUpPage() {
        return "signup";
    }

    // 2. Kuchakata data za Sign-Up (Uhakiki na Usajili)
    @PostMapping("/register")
    public String registerVoter(@RequestParam("registration_number") String regNo,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirm_password") String confirmPassword,
                                Model model) {

        String cleanRegNo = regNo.trim();

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match!");
            return "signup";
        }

        // Uhakiki wa kwanza: Je namba ipo chuo?
        Optional<validStudent> validStudentOpt = validStudentRepository.findByRegistrationNumber(cleanRegNo);
        if (validStudentOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid Registration Number! You are not in the university list.");
            return "signup";
        }

        validStudent validStudent = validStudentOpt.get();
        if (validStudent.isRegistered()) {
            model.addAttribute("errorMessage", "An account with this Registration Number already exists.");
            return "signup";
        }

        // Hifadhi mwanafunzi mpya
        Voter voter = new Voter();
        voter.setRegistrationNumber(cleanRegNo);
        voter.setEmail(email);
        voter.setPasswordHash(passwordEncoder.encode(password)); // Ficha Password kwa BCrypt
        voterRepository.save(voter);

        // Alama namba ya usajili kuwa imeshatumika
        validStudent.setRegistered(true);
        validStudentRepository.save(validStudent);

        model.addAttribute("successMessage", "Account created successfully! Please Login.");
        return "index"; // Mrudishe kwenye Home/Login page
    }

    @GetMapping("/form")
    public String showLoginPage() {
        return "form";
    }
    @GetMapping("/index")
    public String showIndexPage() {
        return "index";
    }

    // Kuonyesha ukurasa wa Forgot Password
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot_password";
    }

    // Kuchakata mabadiliko ya password (bila barua pepe - uhakiki ni regNo + email)
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("registration_number") String regNo,
                                @RequestParam("email") String email,
                                @RequestParam("new_password") String newPassword,
                                @RequestParam("confirm_new_password") String confirmNewPassword,
                                Model model) {

        String cleanRegNo = regNo.trim();

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "Password mpya hazifanani.");
            return "forgot_password";
        }

        Optional<Voter> voterOpt = voterRepository.findByRegistrationNumber(cleanRegNo);
        if (voterOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Registration number haipo kwenye mfumo.");
            return "forgot_password";
        }

        Voter voter = voterOpt.get();
        if (voter.getEmail() == null || !voter.getEmail().trim().equalsIgnoreCase(email.trim())) {
            model.addAttribute("errorMessage", "Email haifanani na ile uliyotumia wakati wa Sign Up.");
            return "forgot_password";
        }

        voter.setPasswordHash(passwordEncoder.encode(newPassword));
        voterRepository.save(voter);

        model.addAttribute("successMessage", "Password imebadilishwa kikamilifu! Sasa unaweza kulogin.");
        return "form";
    }

    @PostMapping("/idhinishakura")
    public String loginVoter(@RequestParam("registration_number") String regNo,
                             @RequestParam("password") String password,
                             HttpSession session,
                             Model model) {

        String cleanRegNo = regNo.trim();

        // 1. Tafuta mwanafunzi kwenye jedwali la voters
        Optional<Voter> voterOpt = voterRepository.findByRegistrationNumber(cleanRegNo);

       if (!voterOpt.isPresent()) {
        model.addAttribute("loginError", "Registration number not found");
        return "form";
       }

       Voter voter = voterOpt.get();

       System.out.println("-----JARIBIO JIPYA LA LOGIN-----");
       System.out.println("password ulioandika kwenye form:[" + password.trim() +"]");
       System.out.println("password inayotoka kwenye DB: [" + voter.getPasswordHash() + "]");

       boolean passwordMatches;
       try {
           passwordMatches = voter.getPasswordHash() != null && passwordEncoder.matches(password.trim(), voter.getPasswordHash());
       } catch (IllegalArgumentException ex) {
           // Hii hutokea kama password_hash iliyopo DB si BCrypt halali (mfano iliwekwa kwa mkono/SQL)
           System.out.println("Password hash batili kwenye DB kwa " + cleanRegNo + ": " + ex.getMessage());
           passwordMatches = false;
       }

       if (!passwordMatches){
        System.out.println("Results:password hailingani! Login imekataliwa.");
        model.addAttribute("loginError","Neno siri sio sahihi");
        return "form";
       }

       if (voter.isHasVoted()){
        model.addAttribute("LoginError", "Umeshapiga kura");
        return "form";
       }

       // Tafuta taarifa za chuo/idara za mwanafunzi huyu ili kuchuja wagombea wanaomhusu
       Optional<validStudent> validStudentOpt = validStudentRepository.findByRegistrationNumber(cleanRegNo);
       if (validStudentOpt.isEmpty()) {
        model.addAttribute("loginError", "Taarifa zako za chuo/idara hazikupatikana.");
        return "form";
       }
       validStudent student = validStudentOpt.get();

       session.setAttribute("voterId", voter.getVoterId());
       session.setAttribute("regNo", voter.getRegistrationNumber());
       session.setAttribute("collegeId", student.getCollegeId());
       session.setAttribute("departmentId", student.getDepartmentId());

       System.out.println("MATOKEO : Login imefanikiwa anapelekwa /terms.");
       return "redirect:/terms";
}
}
