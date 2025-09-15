package org.elsveys.controller;

import org.elsveys.dto.TaskDTO;
import org.elsveys.dto.UserDTO;
import org.elsveys.entity.Assignment;
import org.elsveys.entity.Task;
import org.elsveys.entity.User;
import org.elsveys.mapper.TaskDTOMapper;
import org.elsveys.mapper.UserDTOMapper;
import org.elsveys.service.AssignmentService;
import org.elsveys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final UserDTOMapper userMapper;
    private final TaskDTOMapper taskMapper;

    public UserController(UserService userService, AssignmentService assignmentService,
                          UserDTOMapper userMapper, TaskDTOMapper taskMapper){
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.userMapper = userMapper;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userService.getAllUsers();

        List<CompletableFuture<UserDTO>> futures = users.stream()
                .map(user -> CompletableFuture.supplyAsync(() -> {
                    List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(user.getId());

                    List<CompletableFuture<TaskDTO>> taskFutures = userAssignments.stream()
                            .map(assignment -> CompletableFuture.supplyAsync(() -> {
                                Task task = assignment.getTask();
                                List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                                return taskMapper.toTaskDTO(task, taskAssignments);
                            }))
                            .collect(Collectors.toList());

                    List<TaskDTO> taskDTOs = taskFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());

                    return userMapper.toUserDTO(user, taskDTOs);
                }))
                .collect(Collectors.toList());

        List<UserDTO> userDTOs = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id){
        User user = userService.getUser(id);
        List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(id);

        List<CompletableFuture<TaskDTO>> taskFutures = userAssignments.stream()
                .map(assignment -> CompletableFuture.supplyAsync(() -> {
                    Task task = assignment.getTask();
                    List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                    return taskMapper.toTaskDTO(task, taskAssignments);
                }))
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = taskFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userMapper.toUserDTO(user, taskDTOs));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO){
        userDTO.setId(id);
        User updatedUser = userService.updateUser(userMapper.toUser(userDTO));

        List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(id);
        List<CompletableFuture<TaskDTO>> taskFutures = userAssignments.stream()
                .map(assignment -> CompletableFuture.supplyAsync(() -> {
                    Task task = assignment.getTask();
                    List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                    return taskMapper.toTaskDTO(task, taskAssignments);
                }))
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = taskFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userMapper.toUserDTO(updatedUser, taskDTOs));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteUser(@PathVariable Integer id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> restoreUser(@PathVariable Integer id) {
        userService.restoreUser(id);
        User user = userService.getUser(id);

        List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(id);
        List<CompletableFuture<TaskDTO>> taskFutures = userAssignments.stream()
                .map(assignment -> CompletableFuture.supplyAsync(() -> {
                    Task task = assignment.getTask();
                    List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                    return taskMapper.toTaskDTO(task, taskAssignments);
                }))
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = taskFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userMapper.toUserDTO(user, taskDTOs));
    }
}