package com.example.backend.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Chapter;
import com.example.backend.model.Enrollment;
import com.example.backend.model.Professor;
import com.example.backend.model.User;
import com.example.backend.model.Wishlist;
import com.example.backend.service.ChapterService;
import com.example.backend.service.CourseService;
import com.example.backend.service.EnrollmentService;
import com.example.backend.service.ProfessorService;
import com.example.backend.service.UserService;
import com.example.backend.service.WishListService;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProfessorService professorService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private WishListService wishListService;


    @GetMapping("/userlist")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/enrollnewcourse/{email}/{role}")
    public String enrollNewCourse(@RequestBody Enrollment enrollment, @PathVariable String email, @PathVariable String role) throws Exception {
        String enrolledUserName = "", enrolledUserID = "";
        if(role.equalsIgnoreCase("user")){
            List<User> users = userService.getAllUsers();
            for(User userObj:users){
                if(userObj.getEmail().equalsIgnoreCase(email)){
                    enrolledUserName = userObj.getUsername();
                    enrolledUserID = userObj.getUserid();
                    enrollment.setEnrolleduserid(enrolledUserID);
                    enrollment.setEnrolledusername(enrolledUserName);
                    break;
                }
            }
        }
        else if(role.equalsIgnoreCase("professor")){
            List<Professor> professors = professorService.getAllProfessors();
            for(Professor professorObj:professors){
                if(professorObj.getEmail().equalsIgnoreCase(email)){
                    enrolledUserName = professorObj.getProfessorname();
                    enrolledUserID = professorObj.getProfessorid();
                    enrollment.setEnrolleduserid(enrolledUserID);
                    enrollment.setEnrolledusername(enrolledUserName);
                    break;
                }
            }
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        enrollment.setEnrolleddate(todayDate);

        enrollmentService.saveEnrollment(enrollment);

        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        Map<String, Integer> enrolledCount = new LinkedHashMap<>();
        for(Enrollment enrollObj:enrollments){
            String courseName = enrollObj.getCoursename();
            enrolledCount.put(courseName, enrolledCount.getOrDefault(courseName, 0) + 1);
        }
        for(Map.Entry<String, Integer> obj:enrolledCount.entrySet()){
            if(obj.getKey().equalsIgnoreCase(enrollment.getCoursename())){
                enrollmentService.updateEnrolledcount(obj.getKey(), obj.getValue());
                courseService.updateEnrolledcount(obj.getKey(), obj.getValue());
            }
        }
        return "done";
    }

    @GetMapping("/getenrollmentstatus/{coursename}/{email}/{role}")
	public ResponseEntity<Set<String>> getEnrollmentStatus(@PathVariable String coursename, @PathVariable String email, @PathVariable String role) throws Exception
	{
		List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
		User userObj;
		Professor professorObj;
		String enrolledUser = "";
		if(role.equalsIgnoreCase("user"))
		{
		    userObj = userService.fetchUserByEmail(email);
		    enrolledUser = userObj.getUsername();
		}
		else if(role.equalsIgnoreCase("professor"))
		{
		    professorObj = professorService.fetchProfessorByEmail(email);
		    enrolledUser = professorObj.getProfessorname();
		}
		
		Set<String> enrollmentStatus = new LinkedHashSet<>();
		for(Enrollment enrollmentObj : enrollments)
		{
			if(enrollmentObj.getCoursename().equalsIgnoreCase(coursename) && enrollmentObj.getEnrolledusername().equalsIgnoreCase(enrolledUser))
			{
				enrollmentStatus.add("enrolled");
				return new ResponseEntity<>(enrollmentStatus, HttpStatus.OK);
			}
		}
		enrollmentStatus.add("notenrolled");
		return new ResponseEntity<>(enrollmentStatus, HttpStatus.OK);
	}
	
	@PostMapping("/addtowishlist")
	public ResponseEntity<Wishlist> addNewCourse(@RequestBody Wishlist wishlist) throws Exception
	{
		Wishlist wishlistObj = wishListService.addToWishlist(wishlist);
		return new ResponseEntity<>(wishlistObj, HttpStatus.OK);
	}
	
	@GetMapping("/getwishliststatus/{coursename}/{email}")
	public ResponseEntity<Set<String>> getWishlistStatus(@PathVariable String coursename, @PathVariable String email) throws Exception
	{
		List<Wishlist> wishlists = wishListService.getAllLikedCourses();
		Set<String> wishlistsStatus = new LinkedHashSet<>();
		boolean found = false;
		for(Wishlist wishlistsObj : wishlists)
		{
			if(wishlistsObj.getCoursename().equalsIgnoreCase(coursename) && wishlistsObj.getLikeduser().equalsIgnoreCase(email))
			{
				wishlistsStatus.add("liked");
				found = true;
				break;
			}
		}
		if(!found)
		{
			wishlistsStatus.add("notliked");
		}

		return new ResponseEntity<>(wishlistsStatus, HttpStatus.OK);
	}
	
	@GetMapping("/getallwishlist")
	public ResponseEntity<List<Wishlist>> getAllWislist() throws Exception
	{
		List<Wishlist> Wishlists = wishListService.getAllLikedCourses();
		return new ResponseEntity<>(Wishlists, HttpStatus.OK);
	}
	
	@GetMapping("/getwishlistbyemail/{email}")
	public ResponseEntity<List<Wishlist>> getWishlistByEmail(@PathVariable String email) throws Exception
	{
		List<Wishlist> Wishlists = wishListService.fetchByLikeduser(email);
		return new ResponseEntity<>(Wishlists, HttpStatus.OK);
	}
	
	@GetMapping("/getenrollmentbyemail/{email}/{role}")
	public ResponseEntity<List<Enrollment>> getEnrollmentsByEmail(@PathVariable String email, @PathVariable String role) throws Exception
	{
		User userObj;
		Professor professorObj;
		String enrolledUser = "";
		if(role.equalsIgnoreCase("user"))
		{
		    userObj = userService.fetchUserByEmail(email);
		    enrolledUser = userObj.getUsername();
		}
		else if(role.equalsIgnoreCase("professor"))
		{
		    professorObj = professorService.fetchProfessorByEmail(email);
		    enrolledUser = professorObj.getProfessorname();
		}
		
		List<Enrollment> enrollments = enrollmentService.fetchByEnrolledusername(enrolledUser);
		return new ResponseEntity<>(enrollments, HttpStatus.OK);
	}
	
	@GetMapping("/getchapterlistbycoursename/{coursename}")
	public ResponseEntity<List<Chapter>> getChapterListByCoursename(@PathVariable String coursename) throws Exception
	{
		List<Chapter> chapterLists = chapterService.fetchByCoursename(coursename);
		if(chapterLists.isEmpty())
		{
			Chapter obj1 = new Chapter();
			obj1.setChapter1name("");
			obj1.setChapter2name("");
			obj1.setChapter3name("");
			obj1.setChapter4name("");
			obj1.setChapter5name("");
			obj1.setChapter6name("");
			obj1.setChapter7name("");
			obj1.setChapter8name("");
			chapterLists.add(obj1);
		}
		return new ResponseEntity<>(chapterLists, HttpStatus.OK);
	}
	
	@GetMapping("/userprofileDetails/{email}")
	public ResponseEntity<List<User>> getProfileDetails(@PathVariable String email) throws Exception
	{
		List<User> users = userService.fetchProfileByEmail(email);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@PutMapping("/updateuser/{email}")
	public ResponseEntity<User> updateUserProfile(@RequestBody User user, @PathVariable String email) throws Exception
	{
		User userobj = userService.updateUserProfile(email,user);
		return new ResponseEntity<>(userobj, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalusers")
	public ResponseEntity<List<Integer>> getTotalUsers() throws Exception
	{
		List<User> users = userService.getAllUsers();
		List<Integer> usersCount = new ArrayList<>();
		usersCount.add(users.size());
		return new ResponseEntity<>(usersCount, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalenrollmentcount")
	public ResponseEntity<List<Integer>> getTotalEnrollmentcount() throws Exception
	{
		List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
		int count = 0;
		for(Enrollment enrollmentObj : enrollments)
		{
			count += Integer.parseInt(enrollmentObj.getEnrolledcount());
		}
		List<Integer> enrollmentsCount = new ArrayList<>();
		enrollmentsCount.add(count);
		return new ResponseEntity<>(enrollmentsCount, HttpStatus.OK);
	}
	
	@GetMapping("/gettotalenrollments")
	public ResponseEntity<List<Integer>> getTotalEnrollments() throws Exception
	{
		List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
		List<Integer> enrollmentsCount = new ArrayList<>();
		enrollmentsCount.add(enrollments.size());
		return new ResponseEntity<>(enrollmentsCount, HttpStatus.OK);
	}
}
   

