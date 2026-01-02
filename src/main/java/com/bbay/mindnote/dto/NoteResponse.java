package com.bbay.mindnote.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record NoteResponse(
        Long id,
        String title,
        String content,

        // Category details (can be null if no category assigned)
        Long categoryId,
        String categoryName,

        // List of tag names
        Set<String> tags,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
