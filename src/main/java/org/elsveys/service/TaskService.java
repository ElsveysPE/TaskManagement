package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.entity.Task;
import org.elsveys.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskService {
    private final TaskRepository taskRepository;
    private final EntityManager entityManager;
    @Autowired
    public TaskService(TaskRepository taskRepository, EntityManager entityManager){
        this.taskRepository = taskRepository;
        this.entityManager = entityManager;
    }

    public Task getTask(Integer id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public void createTask(Task task){
        taskRepository.save(task);
    }

    public void updateTask(Task task){
        taskRepository.save(task);
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
}
