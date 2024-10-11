package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.Note;
import com.example.backend.repository.NoteRepository;

@Service
public class NoteService {
    @Autowired
    NoteRepository noteRepository;

    public List<Note> ListNoteFromCourse(String courseName,String author) {
        List<Note> notes = noteRepository.findNoteByCourseCoursename(courseName);
        notes.removeIf(note -> !note.getAuthor().equals(author));
        return notes;
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }

    public Note updateNote(Long id, Note note) {
        Note existingNote = noteRepository.findById(id).orElse(null);
        if (existingNote != null) {
            existingNote.setNoteContent(note.getNoteContent());
            return noteRepository.save(existingNote);
        }
        return null;
    }

    public boolean deleteNote(Long id) {
        Note existingNote = noteRepository.findById(id).orElse(null);
        if (existingNote != null) {
            noteRepository.delete(existingNote);
            return true;
        }
        return false;
    }
}
