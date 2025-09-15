package org.elsveys.mapper;

import org.elsveys.dto.ProfileDTO;
import org.elsveys.entity.Credentials;
import org.springframework.stereotype.Component;

@Component
public class ProfileDTOMapper {

    public ProfileDTO toProfileDTO(Credentials credentials) {
        return new ProfileDTO(
                credentials.getAlias(),
                credentials.getUser().getUsername(),
                credentials.getUser().getEmail(),
                credentials.getUser().getSpecialization(),
                credentials.getUser().getUserRole()
        );
    }
}