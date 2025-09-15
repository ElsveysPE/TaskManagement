package org.elsveys.controller;

import org.elsveys.dto.OverdueTaskDTO;
import org.elsveys.entity.Project;
import org.elsveys.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Project>> getAllProjects(){
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('TEAMLEAD')")
    public ResponseEntity<List<Project>> getAllMyProjects(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Integer userId = projectService.getUserIdByUsername(username);

        List<Project> projects = projectService.getAllMyProjects(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<Project> getProject(@PathVariable Integer id){
        Project project = projectService.getProject(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<Project> createProject(@RequestBody Project project){
        Project newProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<Project> updateProject(@PathVariable Integer id, @RequestBody Project project){
        project.setId(id);
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<Void> softDeleteProject(@PathVariable Integer id){
        projectService.softDeleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> restoreProject(@PathVariable Integer id){
        projectService.restoreProject(id);
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @GetMapping("/{id}/unassigned-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<List<String>> getUnassignedTasks(@PathVariable Integer id) {
        List<String> tasks = projectService.findUnassignedTasks(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/overdue-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEAMLEAD')")
    public ResponseEntity<List<OverdueTaskDTO>> getOverdueTasks(@PathVariable Integer id) {
        List<OverdueTaskDTO> tasks = projectService.getOverdueTasks(id);
        return ResponseEntity.ok(tasks);
    }
}