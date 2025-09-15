package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Project;
import org.elsveys.entity.Task;
import org.elsveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Optional<Project> findByProjectId(Integer id);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.isDeleted = false")
    List<Task> findTasksByProjectId(@Param("projectId") Integer projectId);
}
