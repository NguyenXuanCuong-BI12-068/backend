package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Professor;
import com.example.backend.model.User;
import com.example.backend.service.ProfessorService;
import com.example.backend.service.UserService;

@RestController
@CrossOrigin(origins = "https://frontend-nine-delta-58.vercel.app")
public class LoginController {

    @Autowired
    private UserService userService;
   
    @Autowired
    private ProfessorService professorService;

    @Autowired
    private PasswordEncoder passwordEncoder;
   
    @GetMapping("/")
    public String welcomeMessage() {
        return "Welcome to Smart and Dynamic Elearning system !!!";
    }
   
    @PostMapping("/loginuser")
    public ResponseEntity<?> loginUser(@RequestBody User user) throws Exception {
        String currEmail = user.getEmail();
        String currPassword = user.getPassword();
       
        User userObj = userService.fetchUserByEmail(currEmail);
        if(userObj != null && passwordEncoder.matches(currPassword, userObj.getPassword())) {
            return ResponseEntity.ok(userObj);
        }
        return ResponseEntity.badRequest().body("User does not exist or invalid credentials");
    }
   
    @PostMapping("/loginprofessor")
    public ResponseEntity<?> loginProfessor(@RequestBody Professor professor) throws Exception {
        String currEmail = professor.getEmail();
        String currPassword = professor.getPassword();
       
        Professor professorObj = professorService.fetchProfessorByEmail(currEmail);
        if(professorObj != null && passwordEncoder.matches(currPassword, professorObj.getPassword())) {
            return ResponseEntity.ok(professorObj);
        }
        return ResponseEntity.badRequest().body("Professor does not exist or invalid credentials");
    }
}
