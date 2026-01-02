package com.bbay.mindnote.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record NoteRequest(
        @NotBlank(message = "Title is required")
        String title,

        String content,

        // Optional: The ID of the existing category
        Long categoryId,

        // Optional: A set of tag names (e.g., ["Work", "Urgent"])
        // We use names here so the Service can "find or create" them
        Set<String> tags
) {
        // Compact Constructor to ensure collections are never null
        public NoteRequest {
                if (tags == null) {
                        tags = Set.of();
                }
        }
}