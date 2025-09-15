package org.elsveys.controller;

import org.elsveys.request.LoginRequest;
import org.elsveys.request.RegistrationRequest;
import org.elsveys.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest profileInfo) {
        String alias = profileInfo.getAlias();
        String password = profileInfo.getPassword();
        String token = authService.login(alias, password);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest profileInfo) {
        authService.register(
                profileInfo.getUsername(),
                profileInfo.getEmail(),
                profileInfo.getAlias(),
                profileInfo.getPassword(),
                profileInfo.getSpecialization()
        );
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
}