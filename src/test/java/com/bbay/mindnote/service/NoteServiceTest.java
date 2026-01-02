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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

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
        testNote.setTags(new HashSet<>());

        // NoteRequest now takes 4 args
        testRequest = new NoteRequest("Test Note", "Test Content", null, null);
    }

    @Test
    @DisplayName("Should return paged notes (no filters)")
    void getAllNotes_NoFilters_ShouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Note> notePage = new PageImpl<>(List.of(testNote));

        when(noteRepository.findAll(pageable)).thenReturn(notePage);

        // Act
        Page<NoteResponse> result = noteService.getAllNotes(null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testNote.getTitle(), result.getContent().get(0).title());

        verify(noteRepository).findAll(pageable);
        verify(noteRepository, never()).findByFilters(any(), any(), any());
    }

    @Test
    @DisplayName("Should return paged filtered notes")
    void getAllNotes_WithFilters_ShouldReturnFilteredPage() {
        // Arrange
        String category = "Work";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Note> notePage = new PageImpl<>(List.of(testNote));

        when(noteRepository.findByFilters(eq(category), eq(null), eq(pageable))).thenReturn(notePage);

        // Act
        Page<NoteResponse> result = noteService.getAllNotes(category, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(noteRepository).findByFilters(eq(category), eq(null), eq(pageable));
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
    @DisplayName("Should create note successfully without Category or Tags")
    void createNote_ShouldReturnCreatedNote() {
        // Arrange
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        // Act
        NoteResponse result = noteService.createNote(testRequest);

        // Assert
        assertNotNull(result);
        verify(noteRepository, times(1)).save(any(Note.class));
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should create note with Category and Tags")
    void createNote_WithCategoryAndTags_ShouldWork() {
        // Arrange
        Long catId = 100L;
        String tagName = "Java";

        NoteRequest complexRequest = new NoteRequest(
                "Tech Note",
                "Content",
                catId,
                Set.of(tagName)
        );

        Category mockCategory = new Category("Tech");
        mockCategory.setId(catId);
        Tag mockTag = new Tag(tagName);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(mockCategory));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(mockTag));

        Note savedNote = new Note();
        savedNote.setId(1L);
        savedNote.setTitle("Tech Note");
        savedNote.setCategory(mockCategory);
        savedNote.setTags(Set.of(mockTag));
        savedNote.setCreatedAt(LocalDateTime.now());
        savedNote.setUpdatedAt(LocalDateTime.now());

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // Act
        NoteResponse result = noteService.createNote(complexRequest);

        // Assert
        assertNotNull(result);
        assertEquals(catId, result.categoryId());
        assertTrue(result.tags().contains(tagName));

        verify(categoryRepository).findById(catId);
        verify(tagRepository).findByName(tagName);
    }

    @Test
    @DisplayName("Should update note successfully")
    void updateNote_WhenNoteExists_ShouldReturnUpdatedNote() {
        // Arrange
        NoteRequest updateRequest = new NoteRequest("Updated Title", "Updated Content", null, null);

        Note existingNote = testNote;

        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");
        updatedNote.setTags(new HashSet<>());
        updatedNote.setCreatedAt(testNote.getCreatedAt());
        updatedNote.setUpdatedAt(LocalDateTime.now());

        when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        // Act
        NoteResponse result = noteService.updateNote(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.title());

        verify(noteRepository).findById(1L);
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent note")
    void updateNote_WhenNoteDoesNotExist_ShouldThrowException() {
        // Arrange
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> noteService.updateNote(1L, testRequest));

        verify(noteRepository).findById(1L);
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
}