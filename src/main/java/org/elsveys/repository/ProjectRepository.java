package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
