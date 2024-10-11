package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String coursename;
	private String courseid;
	private String enrolleddate;
	private String instructorname;
	private String instructorinstitution;
	private String enrolledcount;
	private String youtubeurl;
	private String websiteurl;
	private String coursetype;
	private String skilllevel;
	private String language;
	private String description;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();
    public Course(int id, String coursename, String courseid, String enrolleddate, String instructorname,
            String instructorinstitution, String enrolledcount, String youtubeurl, String websiteurl, String coursetype,
            String skilllevel, String language, String description, List<Comment> comments) {
        this.id = id;
        this.coursename = coursename;
        this.courseid = courseid;
        this.enrolleddate = enrolleddate;
        this.instructorname = instructorname;
        this.instructorinstitution = instructorinstitution;
        this.enrolledcount = enrolledcount;
        this.youtubeurl = youtubeurl;
        this.websiteurl = websiteurl;
        this.coursetype = coursetype;
        this.skilllevel = skilllevel;
        this.language = language;
        this.description = description;
        this.comments = comments;
    }

    public Course() {
        super();
    }
    
    public int getId() {
        return id;
    }
    public String getCoursename() {
        return coursename;
    }
    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }
    public String getCourseid() {
        return courseid;
    }
    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }
    public String getEnrolleddate() {
        return enrolleddate;
    }
    public void setEnrolleddate(String enrolleddate) {
        this.enrolleddate = enrolleddate;
    }
    public String getInstructorname() {
        return instructorname;
    }
    public void setInstructorname(String instructorname) {
        this.instructorname = instructorname;
    }
    public String getInstructorinstitution() {
        return instructorinstitution;
    }
    public void setInstructorinstitution(String instructorinstitution) {
        this.instructorinstitution = instructorinstitution;
    }
    public String getEnrolledcount() {
        return enrolledcount;
    }
    public void setEnrolledcount(String enrolledcount) {
        this.enrolledcount = enrolledcount;
    }
    public String getYoutubeurl() {
        return youtubeurl;
    }
    public void setYoutubeurl(String youtubeurl) {
        this.youtubeurl = youtubeurl;
    }
    public String getWebsiteurl() {
        return websiteurl;
    }
    public void setWebsiteurl(String websiteurl) {
        this.websiteurl = websiteurl;
    }
    public String getCoursetype() {
        return coursetype;
    }
    public void setCoursetype(String coursetype) {
        this.coursetype = coursetype;
    }
    public String getSkilllevel() {
        return skilllevel;
    }
    public void setSkilllevel(String skilllevel) {
        this.skilllevel = skilllevel;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
