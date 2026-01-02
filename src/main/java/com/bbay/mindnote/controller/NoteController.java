package com.bbay.mindnote.controller;

import com.bbay.mindnote.dto.NoteRequest;
import com.bbay.mindnote.dto.NoteResponse;
import com.bbay.mindnote.service.NoteService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;


@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger logger = LogManager.getLogger(NoteController.class);

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
        logger.info("NoteController initialized");
    }

    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getAllNotes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        logger.info("GET /api/notes - Request params: cat={}, tag={}, page={}", category, tag, pageable.getPageNumber());

        Page<NoteResponse> notes = noteService.getAllNotes(category, tag, pageable);

        logger.info("GET /api/notes - Returned {} notes", notes.getNumberOfElements());
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id) {
        logger.info("GET /api/notes/{} - Received request to fetch note by id", id);
        NoteResponse note = noteService.getNoteById(id);
        logger.info("GET /api/notes/{} - Successfully returned note", id);
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        logger.info("POST /api/notes - Received request to create note");
        logger.debug("POST /api/notes - Request payload: {}", request);
        NoteResponse createdNote = noteService.createNote(request);
        logger.info("POST /api/notes - Successfully created note with id: {}", createdNote.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request) {
        logger.info("PUT /api/notes/{} - Received request to update note", id);
        logger.debug("PUT /api/notes/{} - Request payload: {}", id, request);
        NoteResponse updatedNote = noteService.updateNote(id, request);
        logger.info("PUT /api/notes/{} - Successfully updated note", id);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        logger.info("DELETE /api/notes/{} - Received request to delete note", id);
        noteService.deleteNote(id);
        logger.info("DELETE /api/notes/{} - Successfully deleted note", id);
        return ResponseEntity.noContent().build();
    }
}
