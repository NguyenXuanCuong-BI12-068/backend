package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.Chapter;
import com.example.backend.repository.ChapterRepository;

@Service
public class ChapterService {
    @Autowired
	private ChapterRepository chapterRepo;
	
	public Chapter saveChapter(Chapter chapter)
	{
		return chapterRepo.save(chapter);
	}
	
	public Chapter addNewChapter(Chapter chapter)
	{
		return chapterRepo.save(chapter);
	}
	
	public List<Chapter> getAllChapters()
	{
		return (List<Chapter>)chapterRepo.findAll();
	}
	
	public List<Chapter> fetchByCoursename(String coursename)
	{
		return (List<Chapter>)chapterRepo.findByCoursename(coursename);
	}

	public Chapter addOrUpdateChapter(Chapter chapter) {
        List<Chapter> existingChapters = chapterRepo.findByCoursename(chapter.getCoursename());
        
        if (existingChapters.isEmpty()) {
            // No chapters exist for this course, so create a new entry
            return chapterRepo.save(chapter);
        } else {
            // Update the existing chapter entry for this course
            Chapter existingChapter = existingChapters.get(0);

            // Update only the fields that are not null
            if (chapter.getChapter1name() != null) existingChapter.setChapter1name(chapter.getChapter1name());
            if (chapter.getChapter1id() != null) existingChapter.setChapter1id(chapter.getChapter1id());
            if (chapter.getChapter2name() != null) existingChapter.setChapter2name(chapter.getChapter2name());
            if (chapter.getChapter2id() != null) existingChapter.setChapter2id(chapter.getChapter2id());
            if (chapter.getChapter3name() != null) existingChapter.setChapter3name(chapter.getChapter3name());
            if (chapter.getChapter3id() != null) existingChapter.setChapter3id(chapter.getChapter3id());
            if (chapter.getChapter4name() != null) existingChapter.setChapter4name(chapter.getChapter4name());
            if (chapter.getChapter4id() != null) existingChapter.setChapter4id(chapter.getChapter4id());
            if (chapter.getChapter5name() != null) existingChapter.setChapter5name(chapter.getChapter5name());
            if (chapter.getChapter5id() != null) existingChapter.setChapter5id(chapter.getChapter5id());
            if (chapter.getChapter6name() != null) existingChapter.setChapter6name(chapter.getChapter6name());
            if (chapter.getChapter6id() != null) existingChapter.setChapter6id(chapter.getChapter6id());
            if (chapter.getChapter7name() != null) existingChapter.setChapter7name(chapter.getChapter7name());
            if (chapter.getChapter7id() != null) existingChapter.setChapter7id(chapter.getChapter7id());
            if (chapter.getChapter8name() != null) existingChapter.setChapter8name(chapter.getChapter8name());
            if (chapter.getChapter8id() != null) existingChapter.setChapter8id(chapter.getChapter8id());

            

            return chapterRepo.save(existingChapter);
        }
    }
    public void updateChaptersByCourse(String oldCourseName, String newCourseName) {
        List<Chapter> chapters = chapterRepo.findAllByCoursename(oldCourseName);
        for (Chapter chapter : chapters) {
            chapter.setCoursename(newCourseName);
            chapterRepo.save(chapter);
        }
    }
}
