package org.elsveys.controller;

import org.elsveys.entity.User;
import org.elsveys.service.TaskService;
import org.elsveys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id){
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    //@PreAuthorise
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping
    //@PreAuthorise
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user){
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    //@PreAuthorise
    public ResponseEntity<User> softDeleteUser(@PathVariable Integer id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    //@PreAuthorise
    public ResponseEntity<User> restoreUser(@PathVariable Integer id) {
        userService.restoreUser(id);
        return ResponseEntity.ok(userService.getUser(id));
    }
}
