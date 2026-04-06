package com.trapexoid.eldmatix.repository;

import com.trapexoid.eldmatix.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByNameAndTenantId(String name, String tenantId);
    List<Permission> findByTenantId(String tenantId);
    List<Permission> findByResourceAndTenantId(String resource, String tenantId);
    boolean existsByNameAndTenantId(String name, String tenantId);
}
