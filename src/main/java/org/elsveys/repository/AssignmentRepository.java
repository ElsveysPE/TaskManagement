package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Task;
import org.elsveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByTaskIdAndIsDeleted(Integer taskId, boolean isDeleted);
    List<Assignment> findByUserIdAndIsDeleted(Integer userId, boolean isDeleted);
    @Modifying
    @Query("UPDATE Assignment a SET a.isDeleted = true, a.deletedAt = :deletedAt WHERE a.task.id = :taskId AND a.isDeleted = false")
    void softDeleteByTaskId(@Param("taskId") Integer taskId, @Param("deletedAt") LocalDate deletedAt);
}
