package org.elsveys.controller;

import org.elsveys.dto.OverdueTaskDTO;
import org.elsveys.entity.Project;
import org.elsveys.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    @Autowired
    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(){
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Integer id){
        Project project = projectService.getProject(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project){
        Project newProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Integer id,@RequestBody Project project){
        project.setId(id);
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping
    public ResponseEntity<Project> softDeleteProject(@PathVariable Integer id){
        projectService.softDeleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Project> restoreProject(@PathVariable Integer id){
        projectService.restoreProject(id);
        return ResponseEntity.ok(projectService.getProject(id));
    }


    // Unnecessary verbose
    @GetMapping("/{id}/unassigned-tasks")
    public ResponseEntity<List<String>> getUnassignedTasks(@PathVariable Integer id) {
        List<String> tasks = projectService.findUnassignedTasks(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/overdue-tasks")
    public ResponseEntity<List<OverdueTaskDTO>> getOverdueTasks(@PathVariable Integer id) {
        List<OverdueTaskDTO> tasks = projectService.getOverdueTasks(id);
        return ResponseEntity.ok(tasks);
    }


}
