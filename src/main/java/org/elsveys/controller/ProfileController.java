package org.elsveys.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.elsveys.dto.ProfileDTO;
import org.elsveys.dto.TaskDTO;
import org.elsveys.entity.Assignment;
import org.elsveys.entity.Task;
import org.elsveys.entity.User;
import org.elsveys.mapper.TaskDTOMapper;
import org.elsveys.service.AssignmentService;
import org.elsveys.service.ProfileService;
import org.elsveys.service.UserService;
import org.elsveys.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final TaskDTOMapper taskMapper;

    public ProfileController(ProfileService profileService, JwtUtil jwtUtil,
                             UserService userService, AssignmentService assignmentService,
                             TaskDTOMapper taskMapper) {
        this.profileService = profileService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/me")
    public ProfileDTO getMyProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String alias = jwtUtil.extractAlias(jwt);
        return profileService.getProfile(alias);
    }

    @PutMapping("/me")
    public ProfileDTO updateMyProfile(@RequestHeader("Authorization") String token,
                                      @RequestBody ProfileDTO profileDTO,
                                      @RequestParam String currentPassword) {
        String jwt = token.substring(7);
        String alias = jwtUtil.extractAlias(jwt);
        return profileService.updateProfile(alias, profileDTO, currentPassword);
    }

    @PutMapping("/me/password")
    public void updateMyPassword(@RequestHeader("Authorization") String token,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword) {
        String jwt = token.substring(7);
        String alias = jwtUtil.extractAlias(jwt);
        profileService.updatePassword(alias, currentPassword, newPassword);
    }
    @GetMapping("/me/tasks")
    public List<TaskDTO> getMyTasks(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String alias = jwtUtil.extractAlias(jwt);

        // Get user by alias
        User user = userService.getUserByAlias(alias); // You'll need this method

        // Get assignments for this user
        List<Assignment> userAssignments = assignmentService.getAssignmentsOfUsers(user.getId());

        // Convert to TaskDTOs
        List<CompletableFuture<TaskDTO>> taskFutures = userAssignments.stream()
                .map(assignment -> CompletableFuture.supplyAsync(() -> {
                    Task task = assignment.getTask();
                    List<Assignment> taskAssignments = assignmentService.getUserAssignments(task.getId());
                    return taskMapper.toTaskDTO(task, taskAssignments);
                }))
                .collect(Collectors.toList());

        return taskFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}