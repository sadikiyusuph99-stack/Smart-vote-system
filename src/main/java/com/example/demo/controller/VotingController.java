package com.example.demo.controller;

import com.example.demo.model.Vote;
import com.example.demo.model.Voter;
import com.example.demo.repository.CandidateRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.repository.VoterRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.repository.validStudentRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class VotingController {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoterRepository voterRepository;

    // 1. Njia ya kuelekea Ukurasa wa Terms
    @GetMapping("/terms")
    public String showTermsPage(HttpSession session) {
        if (session.getAttribute("voterId") == null) {
            return "redirect:/";
        }
        return "terms";
    }

    // 2. Kuchuja na kuonyesha Wagombea kwenye ballot page
    @GetMapping("/6")
    public String showBallotPaper(HttpSession session, Model model) {
        Long voterId = (Long) session.getAttribute("voterId");
        if (voterId == null) {
            return "redirect:/";
        }

        // Uhakiki: Kama mwanafunzi alishapiga kura tayari, usimruhusu kuingia tena
        Voter voter = voterRepository.findById(voterId).get();
        if (voter.isHasVoted()) {
            model.addAttribute("loginError", "You have already cast your vote! Access denied.");
            session.invalidate(); // Clear session
            return "index";
        }

        Long collegeId = (Long) session.getAttribute("collegeId");
        Long departmentId = (Long) session.getAttribute("departmentId");

        var allCandidates = candidateRepository.findAll();

        // Kuchuja wagombea kwa kutumia Java Streams
        var presidents = allCandidates.stream()
                .filter(c -> "President".equals(c.getPositionType()))
                .collect(Collectors.toList());

        var collegeMps = allCandidates.stream()
                .filter(c -> "College_MP".equals(c.getPositionType()) && collegeId != null && collegeId.equals(c.getCollegeId()))
                .collect(Collectors.toList());

        var departmentMps = allCandidates.stream()
                .filter(c -> "Department_MP".equals(c.getPositionType()) && departmentId != null && departmentId.equals(c.getDepartmentId()))
                .collect(Collectors.toList());

        model.addAttribute("presidents", presidents);
        model.addAttribute("collegeMps", collegeMps);
        model.addAttribute("departmentMps", departmentMps);

        return "6";
    }

    // 3. Kuchakata kura zilizopigwa (Submit Vote)
    @PostMapping("/submitVote")
    public String processVotes(@RequestParam("president_vote") Long presidentCandidateId,
                               @RequestParam("college_vote") Long collegeMpCandidateId,
                               @RequestParam("dept_vote") Long deptMpCandidateId,
                               HttpSession session,
                               Model model) {
        
        Long voterId = (Long) session.getAttribute("voterId");
        if (voterId == null) {
            return "redirect:/6";
        }

        Optional<Voter> voterOpt = voterRepository.findById(voterId);
        if (voterOpt.isPresent()) {
            Voter voter = voterOpt.get();

            // Double check kuzuia udukuzi
            if (voter.isHasVoted()) {
                session.invalidate();
                return "redirect:/";
            }

            // HATUA YA A: Hifadhi kura tatu kwenye jedwali la 'votes'
            Vote presVote = new Vote();
            presVote.setCandidateId(presidentCandidateId);
            presVote.setPositionType("President");
            voteRepository.save(presVote);

            Vote collegeVote = new Vote();
            collegeVote.setCandidateId(collegeMpCandidateId);
            collegeVote.setPositionType("College_MP");
            voteRepository.save(collegeVote);

            Vote deptVote = new Vote();
            deptVote.setCandidateId(deptMpCandidateId);
            deptVote.setPositionType("Department_MP");
            voteRepository.save(deptVote);

            // HATUA YA B: Funga akaunti ya mwanafunzi (hasVoted = true)
            voter.setHasVoted(true);
            voterRepository.save(voter);

            // HATUA YA C: Safisha session ili kuzuia kubofya 'Back' kwenye Browser
            session.invalidate();

            return "redirect:/success";
        }

        return "redirect:/form";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success";
    }
}
