// ProfileService.java
package org.elsveys.service;

import org.elsveys.dto.ProfileDTO;
import org.elsveys.entity.Credentials;
import org.elsveys.mapper.ProfileDTOMapper;
import org.elsveys.repository.CredentialsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {
    private final CredentialsRepository credentialsRepository;
    private final ProfileDTOMapper profileMapper;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(CredentialsRepository credentialsRepository,
                          ProfileDTOMapper profileMapper,
                          PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.profileMapper = profileMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileDTO getProfile(String alias) {
        Credentials credentials = credentialsRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return profileMapper.toProfileDTO(credentials);
    }

    public ProfileDTO updateProfile(String alias, ProfileDTO profileDTO, String currentPassword) {
        Credentials credentials = credentialsRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, credentials.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        credentials.setAlias(profileDTO.getAlias());
        credentials.getUser().setUsername(profileDTO.getUsername());
        credentials.getUser().setEmail(profileDTO.getEmail());

        credentialsRepository.save(credentials);
        return profileMapper.toProfileDTO(credentials);
    }

    public void updatePassword(String alias, String currentPassword, String newPassword) {
        Credentials credentials = credentialsRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, credentials.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        credentials.setPassword(passwordEncoder.encode(newPassword));
        credentialsRepository.save(credentials);
    }
}