package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.entity.Credentials;
import org.elsveys.entity.User;
import org.elsveys.repository.CredentialsRepository;
import org.elsveys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public UserService(UserRepository userRepository, EntityManager entityManager){
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public User getUser(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public void softDeleteUser(Integer id){
        entityManager.createNativeQuery("CALL soft_delete_user(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restoreUser(Integer id){
        entityManager.createNativeQuery("CALL restore_user(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Integer countTasks(Integer id){
        return (Integer) entityManager.createNativeQuery("SELECT count_tasks(:id)")
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserByAlias(String alias) {
        return userRepository.findByCredentialsAlias(alias)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
