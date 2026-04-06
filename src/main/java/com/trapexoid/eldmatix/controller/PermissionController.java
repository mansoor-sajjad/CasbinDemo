package com.trapexoid.eldmatix.controller;

import com.trapexoid.eldmatix.dto.PermissionDto;
import com.trapexoid.eldmatix.model.Permission;
import com.trapexoid.eldmatix.repository.PermissionRepository;
import com.trapexoid.eldmatix.security.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Get all permissions for the current tenant
     */
    @GetMapping
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        String tenantId = TenantContext.currentTenant();
        List<Permission> permissions = permissionRepository.findByTenantId(tenantId);
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDtos);
    }

    /**
     * Get permission by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getPermissionById(@PathVariable Integer id) {
        String tenantId = TenantContext.currentTenant();
        Optional<Permission> permission = permissionRepository.findById(id);
        
        if (permission.isPresent() && permission.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.ok(convertToDto(permission.get()));
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get permissions by resource
     */
    @GetMapping("/resource/{resource}")
    public ResponseEntity<List<PermissionDto>> getPermissionsByResource(@PathVariable String resource) {
        String tenantId = TenantContext.currentTenant();
        List<Permission> permissions = permissionRepository.findByResourceAndTenantId(resource, tenantId);
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDtos);
    }

    /**
     * Create a new permission
     */
    @PostMapping
    public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionDto permissionDto) {
        String tenantId = TenantContext.currentTenant();
        
        // Check if permission already exists
        if (permissionRepository.existsByNameAndTenantId(permissionDto.getName(), tenantId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Permission with name '" + permissionDto.getName() + "' already exists"));
        }

        Permission permission = new Permission(
            permissionDto.getName(),
            permissionDto.getDescription(),
            permissionDto.getResource(),
            permissionDto.getAction(),
            tenantId
        );

        Permission savedPermission = permissionRepository.save(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedPermission));
    }

    /**
     * Update permission
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Integer id, @Valid @RequestBody PermissionDto permissionDto) {
        String tenantId = TenantContext.currentTenant();
        Optional<Permission> permissionOptional = permissionRepository.findById(id);

        if (permissionOptional.isEmpty() || !permissionOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.notFound().build();
        }

        Permission permission = permissionOptional.get();
        permission.setName(permissionDto.getName());
        permission.setDescription(permissionDto.getDescription());
        permission.setResource(permissionDto.getResource());
        permission.setAction(permissionDto.getAction());

        Permission updatedPermission = permissionRepository.save(permission);
        return ResponseEntity.ok(convertToDto(updatedPermission));
    }

    /**
     * Delete permission
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Integer id) {
        String tenantId = TenantContext.currentTenant();
        Optional<Permission> permissionOptional = permissionRepository.findById(id);

        if (permissionOptional.isEmpty() || !permissionOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.notFound().build();
        }

        permissionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PermissionDto convertToDto(Permission permission) {
        return new PermissionDto(
            permission.getId(),
            permission.getName(),
            permission.getDescription(),
            permission.getResource(),
            permission.getAction()
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
