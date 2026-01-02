package com.bbay.mindnote.dto;

import com.bbay.mindnote.entity.TaskPriority;
import com.bbay.mindnote.entity.TaskStatus;
import java.time.LocalDateTime;
import java.util.Set;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate,
        LocalDateTime completedAt,
        Long categoryId,
        String categoryName,
        Set<String> tags,
        Long noteId,
        String noteTitle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}