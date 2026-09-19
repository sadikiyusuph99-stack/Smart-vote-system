package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "position_type", nullable = false, length = 30)
    private String positionType;

    @Column(name = "voted_at")
    private LocalDateTime votedAt = LocalDateTime.now();

    // Getters and Setters
    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public String getPositionType() { return positionType; }
    public void setPositionType(String positionType) { this.positionType = positionType; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}