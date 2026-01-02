package com.bbay.mindnote.repository;

import com.bbay.mindnote.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds notes matching filters with pagination support.
     */
    @Query("SELECT DISTINCT n FROM Note n " +
            "LEFT JOIN n.category c " +
            "LEFT JOIN n.tags t " +
            "WHERE (:category IS NULL OR c.name = :category) " +
            "AND (:tag IS NULL OR t.name = :tag)")
    Page<Note> findByFilters(@Param("category") String category,
                             @Param("tag") String tag,
                             Pageable pageable);
}