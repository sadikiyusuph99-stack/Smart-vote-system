package com.example.demo.repository;

import com.example.demo.model.validStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface validStudentRepository extends JpaRepository<validStudent, String> {
    
    @Query(value = "SELECT * FROM valid_students WHERE registration_number = ?1", nativeQuery = true)
    Optional<validStudent> findByRegistrationNumber(String registrationNumber);
}
