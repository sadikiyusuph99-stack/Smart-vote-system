package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long phone;

    private String level;

    private String health;

    private String teacher;

    //web argument constructor
    public Student(){
    }
    //getters and setters
    //setters
    public void setName(String name){
        this.name = name;
    }
    public void setPhone(Long phone){
        this.phone=phone;
    }
    public void setLevel(String level){
        this.level=level;
    }
    public void setHealth(String health){
        this.health=health;
    }
    public void setTeacher(String teacher){
        this.teacher=teacher;
    }
    //getters
    public String getName(){
        return name;
    }
    public Long getPhone(){
        return phone;
    }
    public String getLevel(){
        return level;
    }
    public String getHealth(){
        return health;
    }
    public String getTeacher(){
        return teacher;
    }

    public Long getId(){
        return id;
    }
}
