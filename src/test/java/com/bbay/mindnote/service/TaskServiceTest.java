package com.bbay.mindnote.service;

import com.bbay.mindnote.dto.TaskRequest;
import com.bbay.mindnote.dto.TaskResponse;
import com.bbay.mindnote.entity.*;
import com.bbay.mindnote.exception.ResourceNotFoundException;
import com.bbay.mindnote.repository.CategoryRepository;
import com.bbay.mindnote.repository.NoteRepository;
import com.bbay.mindnote.repository.TagRepository;
import com.bbay.mindnote.repository.TaskRepository;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskRequest testRequest;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setTags(new HashSet<>());
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        // Basic request without relations
        testRequest = new TaskRequest(
                "Test Task",
                "Desc",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("Should return paged tasks (no filters)")
    void getAllTasks_NoFilters_ShouldReturnAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        // Act
        Page<TaskResponse> result = taskService.getAllTasks(null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(pageable);
        verify(taskRepository, never()).findByFilters(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should return filtered tasks")
    void getAllTasks_WithFilters_ShouldCallFindByFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));
        TaskStatus status = TaskStatus.TODO;

        when(taskRepository.findByFilters(eq(status), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(taskPage);

        // Act
        Page<TaskResponse> result = taskService.getAllTasks(status, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByFilters(eq(status), isNull(), isNull(), isNull(), eq(pageable));
    }

    @Test
    @DisplayName("Should create task successfully without relations")
    void createTask_Basic_ShouldSuccess() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse result = taskService.createTask(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should create task with full relations (Category, Tags, Note)")
    void createTask_FullRelations_ShouldSuccess() {
        // Arrange
        Long catId = 10L;
        Long noteId = 20L;
        String tagName = "Urgent";

        TaskRequest fullRequest = new TaskRequest(
                "Full Task", "Desc",
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDateTime.now(),
                catId, Set.of(tagName), noteId
        );

        Category mockCategory = new Category("Work");
        mockCategory.setId(catId);

        Note mockNote = new Note();
        mockNote.setId(noteId);
        mockNote.setTitle("Meeting Note");

        Tag mockTag = new Tag(tagName);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(mockCategory));
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(mockNote));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(mockTag));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Full Task");
        savedTask.setCategory(mockCategory);
        savedTask.setNote(mockNote);
        savedTask.setTags(Set.of(mockTag));
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse result = taskService.createTask(fullRequest);

        // Assert
        assertNotNull(result);
        assertEquals(catId, result.categoryId());
        assertEquals(noteId, result.noteId());
        assertTrue(result.tags().contains(tagName));

        verify(categoryRepository).findById(catId);
        verify(noteRepository).findById(noteId);
        verify(tagRepository).findByName(tagName);
    }

    @Test
    @DisplayName("Should set completedAt when status changes to DONE")
    void updateTask_SetDone_ShouldSetCompletedAt() {
        // Arrange
        TaskRequest doneRequest = new TaskRequest(
                "Task", "Desc", TaskStatus.DONE, TaskPriority.MEDIUM, null, null, null, null
        );

        // Existing task is TODO
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setTags(new HashSet<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse result = taskService.updateTask(1L, doneRequest);

        // Assert
        assertEquals(TaskStatus.DONE, result.status());
        assertNotNull(result.completedAt(), "CompletedAt should be set when status becomes DONE");
    }

    @Test
    @DisplayName("Should clear completedAt when status changes from DONE to TODO")
    void updateTask_Reopen_ShouldClearCompletedAt() {
        // Arrange
        TaskRequest todoRequest = new TaskRequest(
                "Task", "Desc", TaskStatus.TODO, TaskPriority.MEDIUM, null, null, null, null
        );

        // Existing task is DONE
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus(TaskStatus.DONE);
        existingTask.setCompletedAt(LocalDateTime.now());
        existingTask.setTags(new HashSet<>());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse result = taskService.updateTask(1L, todoRequest);

        // Assert
        assertEquals(TaskStatus.TODO, result.status());
        assertNull(result.completedAt(), "CompletedAt should be null when status is not DONE");
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_WhenExists_ShouldDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void deleteTask_WhenNotExists_ShouldThrowException() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, never()).deleteById(anyLong());
    }
}