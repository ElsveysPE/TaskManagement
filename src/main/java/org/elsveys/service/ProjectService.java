package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.dto.OverdueTaskDTO;
import org.elsveys.entity.Project;
import org.elsveys.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;
    @Autowired
    public ProjectService(ProjectRepository projectRepository ,EntityManager entityManager){
        this.projectRepository = projectRepository;
        this.entityManager = entityManager;
    }

    public Project getProject(Integer id){
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void createProject(Project project){
        projectRepository.save(project);
    }

    public void updateProject(Project project){
        projectRepository.save(project);
    }

    public void softDeleteProject(Integer id){
        entityManager.createNativeQuery("CALL soft_delete_project(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restoreProject(Integer id){
        entityManager.createNativeQuery("CALL restore_project(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Set<String> findUnassignedTasks(Integer id){
        Set<String> result = new HashSet<>();
        List<?> rows = entityManager.createNativeQuery("SELECT find_unassigned_tasks(:id)")
                .setParameter("id", id)
                .getResultList();
        for(Object row : rows){
            result.add(row.toString());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<OverdueTaskDTO> getOverdueTasks(Integer id){
        List<Object[]> rows = entityManager.createNativeQuery("SELECT * FROM overdue_tasks(:id)")
                .setParameter("id", id)
                .getResultList();
        return rows.stream()
                .map(row -> new OverdueTaskDTO(
                        (String) row[0],
                        (String) row[1],
                        (Integer) row[2]
                ))
                .collect(Collectors.toList());
    }

    public double getProjectDonePercentage(Integer id){
        Number result = (Number) entityManager.createNativeQuery("SELECT project_done_percentage(:id)")
                .setParameter("id", id)
                .getSingleResult();
        return result.doubleValue();
    }

}
