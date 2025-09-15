package org.elsveys.service;

import org.elsveys.entity.Credentials;
import org.elsveys.entity.User;
import org.elsveys.enums.UserRole;
import org.elsveys.repository.CredentialsRepository;
import org.elsveys.repository.UserRepository;
import org.elsveys.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       CredentialsRepository credentialsRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String alias, String password) {
        Credentials credentials = credentialsRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, credentials.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (credentials.isDeleted() || credentials.getUser().isDeleted()) {
            throw new RuntimeException("Invalid credentials");
        }

        String role = credentials.getUser().getUserRole().toString();
        return jwtUtil.generateToken(alias, role);
    }

    public void register(String username, String email, String alias,
                         String password, String specialization) {

        if (credentialsRepository.findByAlias(alias).isPresent()) {
            throw new RuntimeException("Please, select a different alias");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setSpecialization(specialization);
        user.setUserRole(UserRole.USER);
        user.setDeleted(false);

        User savedUser = userRepository.save(user);

        Credentials credentials = new Credentials();
        credentials.setAlias(alias);
        credentials.setPassword(passwordEncoder.encode(password));
        credentials.setUser(savedUser);
        credentials.setDeleted(false);

        credentialsRepository.save(credentials);

    }
}
