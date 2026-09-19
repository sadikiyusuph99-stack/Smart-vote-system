package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "valid_students")
public class validStudent {

    @Id
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "college_id", nullable = false)
    private Long collegeId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "is_registered", nullable = false)
    private boolean isRegistered = false;

    // Getters and Setters
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public boolean isRegistered() { return isRegistered; }
    public void setRegistered(boolean isRegistered) { this.isRegistered = isRegistered; }
}