package com.bbay.mindnote.service;

import com.bbay.mindnote.dto.NoteRequest;
import com.bbay.mindnote.dto.NoteResponse;
import com.bbay.mindnote.entity.Category;
import com.bbay.mindnote.entity.Note;
import com.bbay.mindnote.entity.Tag;
import com.bbay.mindnote.exception.ResourceNotFoundException;
import com.bbay.mindnote.repository.CategoryRepository;
import com.bbay.mindnote.repository.NoteRepository;
import com.bbay.mindnote.repository.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private static final Logger logger = LogManager.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public NoteService(NoteRepository noteRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        logger.info("NoteService initialized with Category and Tag support");
    }

    @Transactional(readOnly = true)
    public Page<NoteResponse> getAllNotes(String category, String tag, Pageable pageable) {
        logger.info("Fetching notes - category: {}, tag: {}, page: {}",
                category, tag, pageable.getPageNumber());

        Page<Note> notesPage;

        if (category == null && tag == null) {
            // Use standard JPA repository method for non-filtered request
            notesPage = noteRepository.findAll(pageable);
        } else {
            // Use custom query for filtered request
            notesPage = noteRepository.findByFilters(category, tag, pageable);
        }

        logger.debug("Retrieved {} notes (Total: {})",
                notesPage.getNumberOfElements(), notesPage.getTotalElements());

        // Convert Page<Note> to Page<NoteResponse>
        return notesPage.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(Long id) {
        logger.info("Fetching note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Note not found with id: {}", id);
                    return new ResourceNotFoundException("Note", id);
                });
        logger.debug("Successfully retrieved note with id: {}", id);
        return mapToResponse(note);
    }

    @Transactional
    public NoteResponse createNote(NoteRequest request) {
        logger.info("Creating new note with title: {}", request.title());

        Note note = new Note();
        note.setTitle(request.title());
        note.setContent(request.content());

        // Handle Category
        assignCategory(note, request.categoryId());

        // Handle Tags (Find or Create)
        Set<Tag> tags = resolveTags(request.tags());
        note.setTags(tags);

        Note savedNote = noteRepository.save(note);
        logger.info("Successfully created note with id: {}", savedNote.getId());
        return mapToResponse(savedNote);
    }

    @Transactional
    public NoteResponse updateNote(Long id, NoteRequest request) {
        logger.info("Updating note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot update - Note not found with id: {}", id);
                    return new ResourceNotFoundException("Note", id);
                });

        note.setTitle(request.title());
        note.setContent(request.content());

        // Handle Category Update
        assignCategory(note, request.categoryId());

        // Handle Tags Update (Replace existing tags with new set)
        Set<Tag> tags = resolveTags(request.tags());
        note.setTags(tags);

        Note updatedNote = noteRepository.save(note);
        logger.info("Successfully updated note with id: {}", id);
        return mapToResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        logger.info("Deleting note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot delete - Note not found with id: {}", id);
                    return new ResourceNotFoundException("Note", id);
                });
        noteRepository.delete(note);
        logger.info("Successfully deleted note with id: {}", id);
    }

    // --- Helper Methods ---

    private void assignCategory(Note note, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
            note.setCategory(category);
        } else {
            note.setCategory(null);
        }
    }

    private Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            // Find existing tag by name or create a new one
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));
            tags.add(tag);
        }
        return tags;
    }

    private NoteResponse mapToResponse(Note note) {
        // Extract Category info safely
        Long catId = (note.getCategory() != null) ? note.getCategory().getId() : null;
        String catName = (note.getCategory() != null) ? note.getCategory().getName() : null;

        // Extract Tag names
        Set<String> tagNames = note.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                catId,
                catName,
                tagNames,
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}