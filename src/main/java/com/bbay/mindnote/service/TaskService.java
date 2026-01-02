package com.bbay.mindnote.service;

import com.bbay.mindnote.dto.TaskRequest;
import com.bbay.mindnote.dto.TaskResponse;
import com.bbay.mindnote.entity.*;
import com.bbay.mindnote.exception.ResourceNotFoundException;
import com.bbay.mindnote.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LogManager.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;

    public TaskService(TaskRepository taskRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       NoteRepository noteRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.noteRepository = noteRepository;
        logger.info("TaskService initialized");
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskStatus status, Long categoryId, String tagName, Long noteId, Pageable pageable) {
        logger.info("Fetching tasks with filters");
        Page<Task> tasks;
        
        if (status == null && categoryId == null && tagName == null && noteId == null) {
            tasks = taskRepository.findAll(pageable);
        } else {
            tasks = taskRepository.findByFilters(status, categoryId, tagName, noteId, pageable);
        }
        
        return tasks.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        logger.info("Creating task: {}", request.title());
        Task task = new Task();
        updateTaskFromRequest(task, request);
        
        // Default logic for creation
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        logger.info("Updating task: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        
        updateTaskFromRequest(task, request);
        
        // Handle specific status changes logic
        if (request.status() != null) {
            task.setStatus(request.status());
            if (request.status() == TaskStatus.DONE && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            } else if (request.status() != TaskStatus.DONE) {
                task.setCompletedAt(null);
            }
        }
        
        if (request.priority() != null) task.setPriority(request.priority());
        
        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        logger.info("Deleting task: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task", id);
        }
        taskRepository.deleteById(id);
    }

    private void updateTaskFromRequest(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());

        // Category
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        // Tags (Find or Create)
        Set<Tag> tags = new HashSet<>();
        if (request.tags() != null) {
            for (String name : request.tags()) {
                Tag tag = tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new Tag(name)));
                tags.add(tag);
            }
        }
        task.setTags(tags);

        // Note Link
        if (request.noteId() != null) {
            Note note = noteRepository.findById(request.noteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Note", request.noteId()));
            task.setNote(note);
        } else {
            task.setNote(null);
        }
    }

    private TaskResponse mapToResponse(Task task) {
        String catName = (task.getCategory() != null) ? task.getCategory().getName() : null;
        Long catId = (task.getCategory() != null) ? task.getCategory().getId() : null;
        
        Set<String> tags = task.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        
        Long noteId = (task.getNote() != null) ? task.getNote().getId() : null;
        String noteTitle = (task.getNote() != null) ? task.getNote().getTitle() : null;

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCompletedAt(),
                catId,
                catName,
                tags,
                noteId,
                noteTitle,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}