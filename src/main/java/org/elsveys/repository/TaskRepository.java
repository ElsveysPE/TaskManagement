package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}
