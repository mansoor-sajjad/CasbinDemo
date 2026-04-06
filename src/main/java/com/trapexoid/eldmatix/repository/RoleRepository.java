package com.trapexoid.eldmatix.repository;

import com.trapexoid.eldmatix.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNameAndTenantId(String name, String tenantId);
    List<Role> findByTenantId(String tenantId);
    boolean existsByNameAndTenantId(String name, String tenantId);
}
