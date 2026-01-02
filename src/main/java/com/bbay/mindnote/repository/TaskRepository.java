package com.bbay.mindnote.repository;

import com.bbay.mindnote.entity.Task;
import com.bbay.mindnote.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN t.category c " +
           "LEFT JOIN t.tags tag " +
           "WHERE (:status IS NULL OR t.status = :status) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "AND (:tagName IS NULL OR tag.name = :tagName) " +
           "AND (:noteId IS NULL OR t.note.id = :noteId)")
    Page<Task> findByFilters(
            @Param("status") TaskStatus status,
            @Param("categoryId") Long categoryId,
            @Param("tagName") String tagName,
            @Param("noteId") Long noteId,
            Pageable pageable
    );
}