package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Wishlist;
@Repository
public interface WishListRepository extends JpaRepository<Wishlist, Long> {
    public Wishlist findByCoursename(String coursename);
	
	public Wishlist findByCourseid(String courseid);
	
	public List<Wishlist> findByLikedusertype(String likedusertype);
	
	public List<Wishlist> findByLikeduser(String likeduser);
	
	public List<Wishlist> findByInstructorname(String instructorname);
	
	public List<Wishlist> findByInstructorinstitution(String instructorinstitution);
	
	public List<Wishlist> findByCoursetype(String coursetype);
	
    public List<Wishlist> findBySkilllevel(String skilllevel);
	
	public List<Wishlist> findByLanguage(String language);
}
