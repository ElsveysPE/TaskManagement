package org.elsveys.controller;

import org.elsveys.entity.Task;
import org.elsveys.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Integer id){
        Task task = taskService.getTask(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    //@PreAuthorize
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        Task newTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping
    //@PreAuthorize
    public ResponseEntity<Task> updateTask(@PathVariable Integer id, @RequestBody Task task){
        task.setId(id);
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize
    public ResponseEntity<Task> softDeleteTask(@PathVariable Integer id){
        taskService.softDeleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> restoreTask(@PathVariable Integer id){
        taskService.restoreTask(id);
        return ResponseEntity.ok(taskService.getTask(id));
    }



}
