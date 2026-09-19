package com.example.demo.repository;

import com.example.demo.model.candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Map;

public interface CandidateRepository extends JpaRepository<candidate, Long> {

    // Amri ya SQL ya kuhesabu kura za kila mgombea kwa kuunganisha majedwali ya candidates na votes
    @Query(value = "SELECT c.id AS id, " +
                   "c.full_name AS name, " +
                   "c.position_type AS position, " +
                   "COUNT(v.vote_id) AS total_votes " +
                   "FROM candidates c " +
                   "LEFT JOIN votes v ON c.id = v.candidate_id " +
                   "GROUP BY c.id, c.full_name, c.position_type " +
                   "ORDER BY c.position_type, total_votes DESC", nativeQuery = true)
    List<Map<String, Object>> getElectionResults();

    List<candidate> findByPositionType(String positionType);

    List<candidate> findByPositionTypeAndCollegeId(String positionType, Long collegeId);

    List<candidate> findByPositionTypeAndDepartmentId(String positionType, Long departmentId);
}