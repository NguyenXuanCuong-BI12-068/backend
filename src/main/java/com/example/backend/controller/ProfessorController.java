package com.example.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Chapter;
import com.example.backend.model.Course;
import com.example.backend.model.Enrollment;
import com.example.backend.model.Professor;
import com.example.backend.model.Wishlist;
import com.example.backend.service.ChapterService;
import com.example.backend.service.CourseService;
import com.example.backend.service.EnrollmentService;
import com.example.backend.service.ProfessorService;
import com.example.backend.service.WishListService;

@RestController
@CrossOrigin(origins = "https://frontend-rho-red-58.vercel.app")
public class ProfessorController {
    @Autowired
	private ProfessorService professorService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private ChapterService chapterService;
	
	@Autowired
	private WishListService wishlistService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EnrollmentService enrollmentService;


	
	
	@GetMapping("/professorlist")
	public ResponseEntity<List<Professor>> getProfessorList() throws Exception
	{
		List<Professor> professors = professorService.getAllProfessors();
		return new ResponseEntity<>(professors, HttpStatus.OK);
	}
	
	@GetMapping("/youtubecourselist")
	public ResponseEntity<List<Course>> getYoutubeCourseList() throws Exception
	{
		List<Course> youtubeCourseList = courseService.fetchByCoursetype("Youtube");
		return new ResponseEntity<>(youtubeCourseList, HttpStatus.OK);
	}
	
	@GetMapping("/websitecourselist")
	public ResponseEntity<List<Course>> getWebsiteCourseList() throws Exception
	{
		List<Course> websiteCourseList = courseService.fetchByCoursetype("Website");
		return new ResponseEntity<>(websiteCourseList, HttpStatus.OK);
	}
	
	@GetMapping("/courselistbyname/{coursename}")
	public ResponseEntity<List<Course>> getCourseListByName(@PathVariable String coursename) throws Exception
	{
		Course courseList = courseService.fetchCourseByCoursename(coursename);
		System.out.println(courseList.getCoursename()+" ");
		List<Course> courselist = new ArrayList<>();
		courselist.add(courseList);
		return new ResponseEntity<>(courselist, HttpStatus.OK);
	}
	
	@GetMapping("/professorlistbyemail/{email}")
	public ResponseEntity<List<Professor>> getProfessorListByEmail(@PathVariable String email) throws Exception
	{
		List<Professor> professors = professorService.getProfessorsByEmail(email);
		return new ResponseEntity<>(professors, HttpStatus.OK);
	}
	
	@PostMapping("/addProfessor")
	public Professor addNewProfessor(@RequestBody Professor professor) throws Exception {
		String currEmail = professor.getEmail();
		String newID = getNewID();
		professor.setProfessorid(newID);

		// Encode the password
		professor.setPassword(passwordEncoder.encode(professor.getPassword()));

		if(currEmail != null && !currEmail.isEmpty()) {
			Professor existingProfessor = professorService.fetchProfessorByEmail(currEmail);
			if(existingProfessor != null) {
				throw new Exception("Professor with " + currEmail + " already exists !!!");
			}
		}

		Professor professorObj = professorService.addNewProfessor(professor);
		professorService.updateStatus(professor.getEmail());
		return professorObj;
	}
	
	@PostMapping("/addCourse")
	public Course addNewCourse(@RequestBody Course course) throws Exception
	{
		String newID = getNewID();
		course.setCourseid(newID);
		
		return courseService.addNewCourse(course);
	}
	@GetMapping("/listCourse/{email}")
	public ResponseEntity<List<Map<String, Object>>> listCoursesByProfessor(@PathVariable String email) {
		Professor professor = professorService.fetchProfessorByEmail(email);
		if (professor == null) {
			return ResponseEntity.notFound().build();
		}

		List<Course> courses = courseService.getCoursesByInstructorName(professor.getProfessorname());
		List<Map<String, Object>> result = new ArrayList<>();

		for (Course course : courses) {
			Map<String, Object> courseInfo = new HashMap<>();
			courseInfo.put("course", course);
			
			List<Enrollment> enrollments = enrollmentService.getAllEnrollmentsByCoursename(course.getCoursename());
			courseInfo.put("enrollments", enrollments);

			result.add(courseInfo);
		}

		return ResponseEntity.ok(result);
	}


	@PutMapping("/editCourse/{email}/{coursename}")
	public ResponseEntity<Course> editCourseByEmailAndCoursename(
		@PathVariable String email,
		@PathVariable String coursename,
		@RequestBody Course updatedCourse) {

		Professor professor = professorService.fetchProfessorByEmail(email);
		if (professor == null) {
			return ResponseEntity.notFound().build();
		}

		Course existingCourse = courseService.fetchCourseByCoursename(coursename);
		if (existingCourse == null || !existingCourse.getInstructorname().equals(professor.getProfessorname())) {
			return ResponseEntity.notFound().build();
		}

		// Update course fields
		if (updatedCourse.getCoursename() != null) existingCourse.setCoursename(updatedCourse.getCoursename());
		if (updatedCourse.getDescription() != null) existingCourse.setDescription(updatedCourse.getDescription());

		// Save updated course
		Course editedCourse = courseService.saveCourse(existingCourse);

		// Update enrollment table
		enrollmentService.updateEnrollmentsByCourse(coursename, existingCourse.getCoursename(), existingCourse.getDescription());

		// Update wishlist table
		wishlistService.updateWishlistByCourse(coursename, existingCourse.getCoursename(), existingCourse.getDescription());
		
		chapterService.updateChaptersByCourse(coursename, existingCourse.getCoursename());
		return ResponseEntity.ok(editedCourse);
	}








	
	@PostMapping("/addnewchapter")
    public Chapter addNewChapters(@RequestBody Chapter chapter) throws Exception {
        return chapterService.addOrUpdateChapter(chapter);
    }

	@GetMapping("/getchapters/{coursename}")
	public ResponseEntity<List<Chapter>> getChapters(@PathVariable String coursename) {
		List<Chapter> chapters = chapterService.fetchByCoursename(coursename);
		if (chapters != null && !chapters.isEmpty()) {
			return ResponseEntity.ok(chapters);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/acceptstatus/{email}")
	public ResponseEntity<List<String>> updateStatus(@PathVariable String email) throws Exception
	{
		professorService.updateStatus(email);
		List<String> al=new ArrayList<>();
		al.add("accepted");
		return new ResponseEntity<>(al,HttpStatus.OK);
	}
	
	@GetMapping("/rejectstatus/{email}")
	public ResponseEntity<List<String>> rejectStatus(@PathVariable String email) throws Exception
	{
		professorService.rejectStatus(email);
		List<String> al=new ArrayList<>();
		al.add("rejected");
		return new ResponseEntity<>(al,HttpStatus.OK);
	}
	
	@GetMapping("/professorprofileDetails/{email}")
	public ResponseEntity<List<Professor>> getProfileDetails(@PathVariable String email) throws Exception
	{
		List<Professor> professors = professorService.fetchProfileByEmail(email);
		return new ResponseEntity<>(professors, HttpStatus.OK);
	}
	
	@PutMapping("/updateprofessor/{email}")
	public ResponseEntity<Professor> updateProfessorProfile(@PathVariable String email, @RequestBody Professor professor) throws Exception {
		Professor updatedProfessor = professorService.updateProfessorProfile(email, professor);
		return new ResponseEntity<>(updatedProfessor, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalprofessors")
	public ResponseEntity<List<Integer>> getTotalProfessors() throws Exception
	{
		List<Professor> professors = professorService.getAllProfessors();
		List<Integer> professorsCount = new ArrayList<>();
		professorsCount.add(professors.size());
		return new ResponseEntity<>(professorsCount, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalchapters")
	public ResponseEntity<List<Integer>> getTotalChapters() throws Exception
	{
		List<Chapter> chapters = chapterService.getAllChapters();
		List<Integer> chaptersCount = new ArrayList<>();
		chaptersCount.add(chapters.size());
		return new ResponseEntity<>(chaptersCount, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalcourses")
	public ResponseEntity<List<Integer>> getTotalCourses() throws Exception
	{
		List<Course> courses = courseService.getAllCourses();
		List<Integer> coursesCount = new ArrayList<>();
		coursesCount.add(courses.size());
		return new ResponseEntity<>(coursesCount, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalwishlist")
	public ResponseEntity<List<Integer>> getTotalWishlist() throws Exception
	{
		List<Wishlist> wishlists = wishlistService.getAllLikedCourses();
		List<Integer> wishlistCount = new ArrayList<>();
		wishlistCount.add(wishlists.size());
		return new ResponseEntity<>(wishlistCount, HttpStatus.OK);
	}
  
	@GetMapping("/getcoursenames/{email}")
	public ResponseEntity<List<String>> getCourseNames(@PathVariable String email) throws Exception {
		// Fetch professor by email
		Professor professor = professorService.fetchProfessorByEmail(email);
		if (professor == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		// Fetch courses of type 'YouTube' for the specific professor
		List<Course> courses = courseService.getCoursesByTypeNameAndInstructor("Youtube", professor.getProfessorname());
		List<String> coursenames = new ArrayList<>();
		for(Course obj : courses) {
			coursenames.add(obj.getCoursename());
		}
		return new ResponseEntity<>(coursenames, HttpStatus.OK);
	}

	@GetMapping("/courses/uploaded-today")
	public ResponseEntity<List<Course>> getCoursesUploadedToday() {
		List<Course> todayCourses = courseService.getCoursesUploadedToday();
		return new ResponseEntity<>(todayCourses, HttpStatus.OK);
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
