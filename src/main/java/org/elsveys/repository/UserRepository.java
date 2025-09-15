package org.elsveys.repository;

import org.elsveys.entity.Assignment;
import org.elsveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT id FROM users WHERE username = :username AND is_deleted = false", nativeQuery = true)
    Integer findIdByUsername(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE u.id = (SELECT c.user.id FROM Credentials c WHERE c.alias = :alias AND c.isDeleted = false) AND u.isDeleted = false")
    Optional<User> findByCredentialsAlias(@Param("alias") String alias);
}
