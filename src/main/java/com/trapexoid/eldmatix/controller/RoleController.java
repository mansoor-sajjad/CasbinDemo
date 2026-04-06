package com.trapexoid.eldmatix.controller;

import com.trapexoid.eldmatix.dto.RoleDto;
import com.trapexoid.eldmatix.model.Permission;
import com.trapexoid.eldmatix.model.Role;
import com.trapexoid.eldmatix.repository.PermissionRepository;
import com.trapexoid.eldmatix.repository.RoleRepository;
import com.trapexoid.eldmatix.security.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleController(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Get all roles for the current tenant
     */
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        String tenantId = TenantContext.currentTenant();
        List<Role> roles = roleRepository.findByTenantId(tenantId);
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }

    /**
     * Get role by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Integer id) {
        String tenantId = TenantContext.currentTenant();
        Optional<Role> role = roleRepository.findById(id);
        
        if (role.isPresent() && role.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.ok(convertToDto(role.get()));
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a new role
     */
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDto roleDto) {
        String tenantId = TenantContext.currentTenant();
        
        // Check if role already exists
        if (roleRepository.existsByNameAndTenantId(roleDto.getName(), tenantId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Role with name '" + roleDto.getName() + "' already exists"));
        }

        Role role = new Role(roleDto.getName(), roleDto.getDescription(), tenantId);

        // Add permissions if provided
        if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(roleDto.getPermissionIds());
            permissions.forEach(role::addPermission);
        }

        Role savedRole = roleRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedRole));
    }

    /**
     * Update role
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @Valid @RequestBody RoleDto roleDto) {
        String tenantId = TenantContext.currentTenant();
        Optional<Role> roleOptional = roleRepository.findById(id);

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.notFound().build();
        }

        Role role = roleOptional.get();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());

        // Update permissions if provided
        if (roleDto.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(roleDto.getPermissionIds());
            role.setPermissions(new HashSet<>(permissions));
        }

        Role updatedRole = roleRepository.save(role);
        return ResponseEntity.ok(convertToDto(updatedRole));
    }

    /**
     * Delete role
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        String tenantId = TenantContext.currentTenant();
        Optional<Role> roleOptional = roleRepository.findById(id);

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.notFound().build();
        }

        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign permission to role
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<?> assignPermissionToRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        String tenantId = TenantContext.currentTenant();
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        Optional<Permission> permissionOptional = permissionRepository.findById(permissionId);

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Role not found"));
        }

        if (permissionOptional.isEmpty() || !permissionOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Permission not found"));
        }

        Role role = roleOptional.get();
        Permission permission = permissionOptional.get();
        
        role.addPermission(permission);
        Role updatedRole = roleRepository.save(role);
        
        return ResponseEntity.ok(convertToDto(updatedRole));
    }

    /**
     * Remove permission from role
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<?> removePermissionFromRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        String tenantId = TenantContext.currentTenant();
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        Optional<Permission> permissionOptional = permissionRepository.findById(permissionId);

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Role not found"));
        }

        if (permissionOptional.isEmpty() || !permissionOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Permission not found"));
        }

        Role role = roleOptional.get();
        Permission permission = permissionOptional.get();
        
        role.removePermission(permission);
        Role updatedRole = roleRepository.save(role);
        
        return ResponseEntity.ok(convertToDto(updatedRole));
    }

    private RoleDto convertToDto(Role role) {
        return new RoleDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toSet())
        );
    }

    // Simple error response class
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
