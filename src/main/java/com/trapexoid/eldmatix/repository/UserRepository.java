package com.trapexoid.eldmatix.repository;

import com.trapexoid.eldmatix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    List<User> findByTenantId(String tenantId);
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
}
