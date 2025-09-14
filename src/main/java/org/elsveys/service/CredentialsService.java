package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.entity.Credentials;
import org.elsveys.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CredentialsService {
    private final CredentialsRepository credentialsRepository;
    private final EntityManager entityManager;

    @Autowired
    public CredentialsService(CredentialsRepository credentialsRepository, EntityManager entityManager){
        this.credentialsRepository = credentialsRepository;
        this.entityManager = entityManager;
    }

    public Credentials getCredentials(Integer id){
        return credentialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Credentials createCredentials(Credentials credentials){
        return credentialsRepository.save(credentials);
    }

    public Credentials updateCredentials(Credentials credentials){
        return credentialsRepository.save(credentials);
    }

    public void softDeleteCredentials(Integer id){
        entityManager.createNativeQuery("CALL soft_delete_credentials(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restoreCredentials(Integer id){
        entityManager.createNativeQuery("CALL restore_credentials(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Credentials> getAllCredentials(){
        return credentialsRepository.findAll();
    }
}
