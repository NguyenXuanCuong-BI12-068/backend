package com.example.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.model.Comment;
import com.example.backend.repository.CommentRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;


    public List<Comment> getCommentsForCourse(String courseName) {
        List<Comment> allComments = commentRepository.findByCourseCoursename(courseName);
        List<Comment> topLevelComments = new ArrayList<>();

        Map<Long, Comment> commentMap = new HashMap<>();
        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);
            if (comment.getParent() == null) {
                topLevelComments.add(comment);
            }
        }

        for (Comment comment : allComments) {
            if (comment.getParent() != null) {
                Comment parent = commentMap.get(comment.getParent().getId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(comment);
                }
            }
        }

        return topLevelComments;
    }
    

    public List<Comment> getAllRootComments() {
        return commentRepository.findByParentIsNull();
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
    public void deleteCommentThread(Long id) {
        Comment rootComment = commentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        
        deleteCommentAndReplies(rootComment);
    }

    private void deleteCommentAndReplies(Comment comment) {
        if (comment.getReplies() != null) {
            for (Comment reply : comment.getReplies()) {
                deleteCommentAndReplies(reply);
            }
        }
        commentRepository.delete(comment);
    }


    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }
}
