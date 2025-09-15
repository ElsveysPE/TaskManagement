package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.dto.OverdueTaskDTO;
import org.elsveys.entity.Project;
import org.elsveys.entity.User;
import org.elsveys.enums.StatusTask;
import org.elsveys.repository.ProjectRepository;
import org.elsveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
            EntityManager entityManager){
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public Project getProject(Integer id){
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Project createProject(Project project){
        return projectRepository.save(project);
    }

    public Project updateProject(Project project){
        return projectRepository.save(project);
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

    public List<String> findUnassignedTasks(Integer id){
        List<String> result = new ArrayList<>();
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

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    public List<Project> getAllMyProjects(Integer teamleadId) {
        return projectRepository.findByTeamleadId(teamleadId);
    }

    public Integer getUserIdByUsername(String username) {
        return userRepository.findIdByUsername(username);
    }
}
