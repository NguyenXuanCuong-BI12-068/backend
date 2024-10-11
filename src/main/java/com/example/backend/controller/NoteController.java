package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Course;
import com.example.backend.model.Note;
import com.example.backend.model.NoteRequest;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.NoteRepository;
import com.example.backend.service.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/note/{courseName}/{author}")
    public ResponseEntity<List<Note>> getNotesForCourse(@PathVariable String courseName, @PathVariable String author) {
        List<Note> notes = noteService.ListNoteFromCourse(courseName,author);
        notes.removeIf(note -> !note.getAuthor().equals(author));
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/addNote/{courseName}/{author}")
    public ResponseEntity<Note> addNote(@RequestBody NoteRequest request,@PathVariable String courseName, @PathVariable String author) {

        Note note = new Note();
        note.setNoteContent(request.getNoteContent());
        note.setAuthor(author);

        Course course = courseRepository.findByCoursename(courseName);
        note.setCourse(course);
        noteRepository.save(note);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/updateNote/{noteId}")
    public Note editNote(@PathVariable Long noteId, @RequestBody NoteRequest request) {
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note == null) {
            return null;
        }
        note.setNoteContent(request.getNoteContent());
        noteRepository.save(note);
        return note;
    }

    @DeleteMapping("/deleteNote/{noteId}")
    public void deleteNote(@PathVariable Long noteId) {
        noteRepository.deleteById(noteId);
    }
}
