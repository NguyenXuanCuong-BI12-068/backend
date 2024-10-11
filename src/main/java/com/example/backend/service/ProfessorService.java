package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.Professor;
import com.example.backend.repository.ProfessorRepository;

@Service
public class ProfessorService {
    @Autowired
	private ProfessorRepository professorRepo;
	
	public Professor saveProfessor(Professor professor)
	{
		return professorRepo.save(professor);
	}
	
	public Professor addNewProfessor(Professor professor)
	{
		return professorRepo.save(professor);
	}
	
	public Professor updateProfessorProfile(String email, Professor professor) throws Exception {
		Professor existingProfessor = professorRepo.findByEmail(email);
		
		if (existingProfessor == null) {
			throw new Exception("Professor not found");
		}
	
		// Update the details
		existingProfessor.setProfessorname(professor.getProfessorname());
		existingProfessor.setEmail(professor.getEmail());
		existingProfessor.setDegreecompleted(professor.getDegreecompleted());
		existingProfessor.setDepartment(professor.getDepartment());
		existingProfessor.setInstitutionname(professor.getInstitutionname());
		existingProfessor.setMobile(professor.getMobile());
		existingProfessor.setGender(professor.getGender());
		existingProfessor.setPassword(professor.getPassword());
		existingProfessor.setExperience(professor.getExperience());
	
		return professorRepo.save(existingProfessor);
	}
	
	public List<Professor> getAllProfessors()
	{
		return (List<Professor>)professorRepo.findAll();
	}
	
	public List<Professor> getProfessorListByEmail(String email) 
	{
		return (List<Professor>)professorRepo.findProfessorListByEmail(email);
	}
	
	public Professor fetchProfessorByEmail(String email)
	{
		return professorRepo.findByEmail(email);
	}
	
	public Professor fetchProfessorByProfessorname(String professorname)
	{
		return professorRepo.findByProfessorname(professorname);
	}
	
	public Professor fetchProfessorByEmailAndPassword(String email, String password)
	{
		return professorRepo.findByEmailAndPassword(email, password);
	}
	
	public List<Professor> fetchProfileByEmail(String email)
	{
		return (List<Professor>)professorRepo.findProfileByEmail(email);
	}

	public void updateStatus(String email) 
	{
		professorRepo.updateStatus(email);
	}

	public void rejectStatus(String email) 
	{
		professorRepo.rejectStatus(email);
	}

	public List<Professor> getProfessorsByEmail(String email)
	{
		return professorRepo.findProfessorListByEmail(email);
	}
}
