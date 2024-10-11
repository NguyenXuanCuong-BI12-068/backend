package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.model.Comment;
import com.example.backend.model.CommentRequest;
import com.example.backend.model.Course;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.CourseRepository;
import com.example.backend.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CourseRepository courseRepository;

    @GetMapping("/course/{courseName}")
    public ResponseEntity<List<Comment>> getCommentsForCourse(@PathVariable String courseName) {
        List<Comment> comments = commentService.getCommentsForCourse(courseName);
        return ResponseEntity.ok(comments);
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllRootComments();
    }

    @PostMapping("/addComments/{courseName}/{author}")
    public ResponseEntity<Comment> addComment(
        @RequestBody CommentRequest request,
        @PathVariable String courseName,
        @PathVariable String author,
        @RequestParam(value = "parentId", required = false) Long parentId) {

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setCreatedBy(request.getCreatedBy());

        Course course = courseRepository.findByCoursename(courseName);
        comment.setCourse(course); // Set the course of the comment

        // Handle parent comment if provided (for replies)
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId).orElse(null);
            if (parentComment != null) {
                comment.setParent(parentComment); // Link to the parent comment
            }
        }

        // Save the comment to the database
        Comment savedComment = commentService.saveComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    @PutMapping("/{id}")
    public Comment editComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Optional<Comment> existingComment = commentService.getCommentById(id);
        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            comment.setContent(updatedComment.getContent());
            return commentService.saveComment(comment);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id, @RequestParam(required = false) Boolean deleteThread) {
        if (Boolean.TRUE.equals(deleteThread)) {
            commentService.deleteCommentThread(id);
        } else {
            commentService.deleteComment(id);
        }
    }
}
