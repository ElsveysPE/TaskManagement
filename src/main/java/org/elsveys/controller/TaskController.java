package org.elsveys.controller;

import org.elsveys.dto.TaskDTO;
import org.elsveys.entity.Assignment;
import org.elsveys.entity.Task;
import org.elsveys.mapper.TaskDTOMapper;
import org.elsveys.service.AssignmentService;
import org.elsveys.service.ProfileService;
import org.elsveys.service.ProjectService;
import org.elsveys.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final AssignmentService assignmentService;
    private final ProjectService projectService;
    private final TaskDTOMapper taskMapper;


    public TaskController(TaskService taskService, AssignmentService assignmentService,
                          ProjectService projectService, TaskDTOMapper taskMapper){
        this.taskService = taskService;
        this.assignmentService = assignmentService;
        this.projectService = projectService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAllTasks(){
        List<Task> tasks = taskService.getAllTasks();

        List<CompletableFuture<TaskDTO>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(() -> {
                    List<Assignment> assignments = assignmentService.getUserAssignments(task.getId()); // FIXED: was getAssignmentsOfUsers
                    return taskMapper.toTaskDTO(task, assignments);
                }))
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<List<TaskDTO>> getProjectTasks(@PathVariable Integer projectId){
        List<Task> tasks = taskService.getAllProjectTasks(projectId);

        List<CompletableFuture<TaskDTO>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(() -> {
                    List<Assignment> assignments = assignmentService.getUserAssignments(task.getId()); // FIXED: was getAssignmentsOfUsers
                    return taskMapper.toTaskDTO(task, assignments);
                }))
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Integer id){
        Task task = taskService.getTask(id);
        TaskDTO taskDTO = taskMapper.toTaskDTO(task, assignmentService.getUserAssignments(task.getId())); // FIXED: was getAssignmentsOfUsers
        return ResponseEntity.ok(taskDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO){
        System.out.println("Creating task: " + taskDTO.getName()); // Your debug line
        Task task = taskMapper.toTask(taskDTO);
        Task newTask = taskService.createTask(task);

        List<Assignment> assignments = taskDTO.getAssignments();
        if(assignments != null) {
            for(Assignment assignment : assignments) {
                assignment.setTask(newTask);
                assignmentService.createAssignment(assignment);
            }
        }

        List<Assignment> createdAssignments = assignmentService.getUserAssignments(newTask.getId()); // FIXED: was getAssignmentsOfUsers
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toTaskDTO(newTask, createdAssignments));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO){
        taskDTO.setId(id);
        Task updatedTask = taskService.updateTask(taskMapper.toTask(taskDTO));

        assignmentService.deleteAssignmentsByTaskId(id);
        List<Assignment> assignments = taskDTO.getAssignments();
        if(assignments != null) {
            for(Assignment assignment : assignments) {
                assignment.setTask(updatedTask);
                assignmentService.createAssignment(assignment);
            }
        }

        List<Assignment> updatedAssignments = assignmentService.getUserAssignments(id); // FIXED: was getAssignmentsOfUsers
        return ResponseEntity.ok(taskMapper.toTaskDTO(updatedTask, updatedAssignments));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Integer id){
        taskService.softDeleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDTO> restoreTask(@PathVariable Integer id){
        taskService.restoreTask(id);
        Task task = taskService.getTask(id);
        List<Assignment> assignments = assignmentService.getUserAssignments(id); // FIXED: was getAssignmentsOfUsers
        return ResponseEntity.ok(taskMapper.toTaskDTO(task, assignments));
    }

    @GetMapping("/my-assignments")
    @PreAuthorize("hasRole('USER') or hasRole('TEAMLEAD') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDTO>> getMyAssignedTasks(Authentication auth){
        String username = auth.getName();

        Integer userId = projectService.getUserIdByUsername(username); // You'll need this method

        List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(userId);

        List<TaskDTO> taskDTOs = userAssignments.stream()
                .map(assignment -> {
                    Task task = assignment.getTask();
                    List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                    return taskMapper.toTaskDTO(task, taskAssignments);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskDTOs);
    }
}