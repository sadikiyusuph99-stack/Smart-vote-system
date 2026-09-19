package com.example.demo.repository;

import com.example.demo.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByRegistrationNumber(String registrationNumber);
}
