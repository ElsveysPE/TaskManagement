package org.elsveys.dto;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Credentials;
import org.elsveys.entity.User;
import org.elsveys.enums.StatusTask;
import org.elsveys.enums.UserRole;

import java.util.List;

public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private UserRole role;
    private String specialization;
    private List<TaskDTO> tasks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public void deleteTask(TaskDTO task) {
        tasks.remove(task);
    }

    public void addTask(TaskDTO task) {
        tasks.add(task);
    }
}
