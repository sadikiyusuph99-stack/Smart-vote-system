package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String positionType;
    private Long collegeId;
    private Long departmentId;
    private String photoPath;
    private String vicePhotoPath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPositionType() { return positionType; }
    public void setPositionType(String positionType) { this.positionType = positionType; }

    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getVicePhotoPath() { return vicePhotoPath; }
    public void setVicePhotoPath(String vicePhotoPath) { this.vicePhotoPath = vicePhotoPath; }
}