package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Professor;
import com.example.backend.model.User;
import com.example.backend.service.ProfessorService;
import com.example.backend.service.UserService;


@RestController
@CrossOrigin(origins = "https://frontend-nine-delta-58.vercel.app")
public class RegistrationController {
    @Autowired
	private UserService userService;
	
	@Autowired
	private ProfessorService professorService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/registeruser")
	public User registerUser(@RequestBody User user) throws Exception {
		String currEmail = user.getEmail();
		String newID = getNewID();
		user.setUserid(newID);
		
		// Encode the password
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		if(currEmail != null && !currEmail.isEmpty()) {
			User existingUser = userService.fetchUserByEmail(currEmail);
			if(existingUser != null) {
				throw new Exception("User with "+currEmail+" already exists !!!");
			}
		}
		return userService.saveUser(user);
	}

	@PostMapping("/registerprofessor")
	public Professor registerDoctor(@RequestBody Professor professor) throws Exception {
		String currEmail = professor.getEmail();
		String newID = getNewID();
		professor.setProfessorid(newID);
		
		// Encode the password
		professor.setPassword(passwordEncoder.encode(professor.getPassword()));
		
		if(currEmail != null && !currEmail.isEmpty()) {
			Professor existingProfessor = professorService.fetchProfessorByEmail(currEmail);
			if(existingProfessor != null) {
				throw new Exception("Professor with "+currEmail+" already exists !!!");
			}
		}
		return professorService.saveProfessor(professor);
	}
	
	public String getNewID()
	{
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"0123456789"+"abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) 
        {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
	}
}
