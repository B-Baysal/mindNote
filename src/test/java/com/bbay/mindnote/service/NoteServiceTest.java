package com.bbay.mindnote.service;

import com.bbay.mindnote.dto.NoteRequest;
import com.bbay.mindnote.dto.NoteResponse;
import com.bbay.mindnote.entity.Note;
import com.bbay.mindnote.exception.ResourceNotFoundException;
import com.bbay.mindnote.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note testNote;
    private NoteRequest testRequest;

    @BeforeEach
    void setUp() {
        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Note");
        testNote.setContent("Test Content");
        testNote.setCreatedAt(LocalDateTime.now());
        testNote.setUpdatedAt(LocalDateTime.now());

        testRequest = new NoteRequest("Test Note", "Test Content");
    }

    @Test
    @DisplayName("Should return all notes")
    void getAllNotes_ShouldReturnAllNotes() {
        // Arrange
        List<Note> notes = List.of(testNote);
        when(noteRepository.findAll()).thenReturn(notes);

        // Act
        List<NoteResponse> result = noteService.getAllNotes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNote.getId(), result.get(0).id());
        assertEquals(testNote.getTitle(), result.get(0).title());
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return note by id when found")
    void getNoteById_WhenNoteExists_ShouldReturnNote() {
        // Arrange
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        // Act
        NoteResponse result = noteService.getNoteById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testNote.getId(), result.id());
        assertEquals(testNote.getTitle(), result.title());
        assertEquals(testNote.getContent(), result.content());
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when note not found")
    void getNoteById_WhenNoteDoesNotExist_ShouldThrowException() {
        // Arrange
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> noteService.getNoteById(1L));
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create note successfully")
    void createNote_ShouldReturnCreatedNote() {
        // Arrange
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        // Act
        NoteResponse result = noteService.createNote(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testNote.getTitle(), result.title());
        assertEquals(testNote.getContent(), result.content());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Should update note successfully")
    void updateNote_WhenNoteExists_ShouldReturnUpdatedNote() {
        // Arrange
        NoteRequest updateRequest = new NoteRequest("Updated Title", "Updated Content");
        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");
        updatedNote.setCreatedAt(testNote.getCreatedAt());
        updatedNote.setUpdatedAt(LocalDateTime.now());

        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        // Act
        NoteResponse result = noteService.updateNote(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.title());
        assertEquals("Updated Content", result.content());
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent note")
    void updateNote_WhenNoteDoesNotExist_ShouldThrowException() {
        // Arrange
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> noteService.updateNote(1L, testRequest));
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    @DisplayName("Should delete note successfully")
    void deleteNote_WhenNoteExists_ShouldDeleteNote() {
        // Arrange
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        doNothing().when(noteRepository).delete(testNote);

        // Act
        noteService.deleteNote(1L);

        // Assert
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).delete(testNote);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent note")
    void deleteNote_WhenNoteDoesNotExist_ShouldThrowException() {
        // Arrange
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> noteService.deleteNote(1L));
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, never()).delete(any(Note.class));
    }
}
