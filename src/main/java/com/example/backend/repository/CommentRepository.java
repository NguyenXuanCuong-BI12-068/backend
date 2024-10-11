package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByParentIsNull();
    List<Comment> findByCourseCoursename(String courseName);
    List<Comment> findByParent(Comment parent);
    
}
