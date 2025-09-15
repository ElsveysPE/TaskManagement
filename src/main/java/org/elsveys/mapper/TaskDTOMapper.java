package org.elsveys.mapper;

import org.elsveys.dto.TaskDTO;
import org.elsveys.dto.UserDTO;
import org.elsveys.entity.Assignment;
import org.elsveys.entity.Project;
import org.elsveys.entity.Task;
import org.elsveys.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.List;
@Component
public class TaskDTOMapper {
    public TaskDTO toTaskDTO(Task task, List<Assignment> assignments){
        TaskDTO newTask = new TaskDTO();
        newTask.setId(task.getId());
        newTask.setName(task.getName());
        newTask.setDescription(task.getDescription());
        newTask.setStatus(task.getStatusTask());
        newTask.setProjectId(task.getProject().getId());
        newTask.setDeleted(task.isDeleted());
        newTask.setAssignments(assignments);
        return newTask;
    }

    public Task toTask(TaskDTO taskDTO) {
        Task task = new Task();
        if(taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatusTask(taskDTO.getStatus());
        task.setDeleted(taskDTO.isDeleted());

        if(taskDTO.getProjectId() == null) {
            throw new RuntimeException("You can`t assign a task to non-existent project");
        }
        Project project = new Project();
        project.setId(taskDTO.getProjectId());
        task.setProject(project);

        return task;
    }

}
