package org.elsveys.mapper;

import org.elsveys.dto.TaskDTO;
import org.elsveys.dto.UserDTO;
import org.elsveys.entity.Credentials;
import org.elsveys.entity.User;
import org.elsveys.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDTOMapper {
    public UserDTO toUserDTO(User user, List<TaskDTO> tasks){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getUserRole());
        userDTO.setSpecialization(user.getSpecialization());
        userDTO.setTasks(tasks);
        return userDTO;
    }

    public User toUser(UserDTO userDTO){
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setUserRole(userDTO.getRole());
        user.setSpecialization(userDTO.getSpecialization());
        return user;
    }
}