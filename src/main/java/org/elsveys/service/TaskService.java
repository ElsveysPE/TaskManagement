package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.entity.Task;
import org.elsveys.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final EntityManager entityManager;

    public TaskService(TaskRepository taskRepository, EntityManager entityManager){
        this.taskRepository = taskRepository;
        this.entityManager = entityManager;
    }

    public Task getTask(Integer id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public Task updateTask(Task task){
        return taskRepository.save(task);
    }

    public void softDeleteTask(Integer id){
        entityManager.createNativeQuery("CALL soft_delete_task(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restoreTask(Integer id){
        entityManager.createNativeQuery("CALL restore_task(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public List<Task> getAllProjectTasks(Integer projectId) {
        return taskRepository.findTasksByProjectId(projectId);
    }
}
