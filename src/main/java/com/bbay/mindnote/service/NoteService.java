package com.bbay.mindnote.service;

import com.bbay.mindnote.dto.NoteRequest;
import com.bbay.mindnote.dto.NoteResponse;
import com.bbay.mindnote.entity.Note;
import com.bbay.mindnote.exception.ResourceNotFoundException;
import com.bbay.mindnote.repository.NoteRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private static final Logger logger = LogManager.getLogger(NoteService.class);

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        logger.info("NoteService initialized");
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes() {
        logger.info("Fetching all notes");
        List<NoteResponse> notes = noteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
        logger.debug("Retrieved {} notes", notes.size());
        return notes;
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
        logger.debug("Note request details - title: {}, content length: {}",
                request.title(),
                request.content() != null ? request.content().length() : 0);

        Note note = new Note();
        note.setTitle(request.title());
        note.setContent(request.content());

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

        logger.debug("Updating note {} - old title: {}, new title: {}",
                id, note.getTitle(), request.title());

        note.setTitle(request.title());
        note.setContent(request.content());

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

    private NoteResponse mapToResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
