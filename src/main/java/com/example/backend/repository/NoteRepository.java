package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findNoteByCourseCoursename(String coursename);
    List<Note> findNoteByAuthor(String author);
}
