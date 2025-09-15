package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
    Optional<Credentials> findByAlias(String alias);
}
