package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;




@RestController
@RequestMapping("api/students")
@CrossOrigin(origins = "*", allowedHeaders= "*",methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,RequestMethod.DELETE})
public class usercontroller {
    
    private final StudentRepository studentRepository;

    public usercontroller(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Student> getAllStudents(){
        return studentRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC,"id"));
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id){
        boolean existing =studentRepository.existsById(id);

        if(!existing){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID "+id+" is not found" );
        }
        studentRepository.deleteById(id);
    }
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id,@RequestBody Student newStudent){
        return studentRepository.findById(id).map(student->{
            if(newStudent.getName() != null && !newStudent.getName().trim().isEmpty()){
                student.setName(newStudent.getName());
            }
            if(newStudent.getTeacher() != null && !newStudent.getTeacher().trim().isEmpty()){
                student.setTeacher(newStudent.getTeacher());
            }
            if(newStudent.getHealth() != null && !newStudent.getHealth().trim().isEmpty()){
                student.setHealth(newStudent.getHealth());
            }
            if(newStudent.getLevel() != null && !newStudent.getLevel().trim().isEmpty()){
                student.setLevel(newStudent.getLevel());
            }
            if(newStudent.getPhone() != null ){
                student.setPhone(newStudent.getPhone());
            }
            return studentRepository.save(student);
        })
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not Found"));
    }
}
    
