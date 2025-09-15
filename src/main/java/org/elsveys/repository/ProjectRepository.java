package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query(value = "SELECT * FROM projects WHERE teamlead_id = :teamleadId AND is_deleted = false", nativeQuery = true)
    List<Project> findByTeamleadId(@Param("teamleadId") Integer teamleadId);
}
