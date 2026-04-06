package com.trapexoid.eldmatix.controller;

import com.trapexoid.eldmatix.dto.RoleDto;
import com.trapexoid.eldmatix.model.Role;
import com.trapexoid.eldmatix.model.User;
import com.trapexoid.eldmatix.repository.RoleRepository;
import com.trapexoid.eldmatix.repository.UserRepository;
import com.trapexoid.eldmatix.security.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserManagementController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Get all users in the current tenant
     */
    @GetMapping
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        String tenantId = TenantContext.currentTenant();
        List<User> users = userRepository.findByTenantId(tenantId);
        List<UserInfoDto> userDtos = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Get user by username
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserInfoDto> getUser(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
            String tenantId = TenantContext.currentTenant();
            if (user.get().getTenantId().equals(tenantId)) {
                return ResponseEntity.ok(convertToDto(user.get()));
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get roles for a user
     */
    @GetMapping("/{username}/roles")
    public ResponseEntity<List<RoleDto>> getUserRoles(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
            String tenantId = TenantContext.currentTenant();
            if (user.get().getTenantId().equals(tenantId)) {
                List<RoleDto> roleDtos = user.get().getRoles().stream()
                        .map(this::convertRoleToDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(roleDtos);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Assign a role to a user
     */
    @PostMapping("/{username}/roles/{roleId}")
    public ResponseEntity<?> assignRoleToUser(@PathVariable String username, @PathVariable Integer roleId) {
        String tenantId = TenantContext.currentTenant();
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found"));
        }

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Role not found"));
        }

        User user = userOptional.get();
        if (!user.getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("User does not belong to current tenant"));
        }

        Role role = roleOptional.get();
        user.addRole(role);
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    /**
     * Remove a role from a user
     */
    @DeleteMapping("/{username}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable String username, @PathVariable Integer roleId) {
        String tenantId = TenantContext.currentTenant();
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found"));
        }

        if (roleOptional.isEmpty() || !roleOptional.get().getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Role not found"));
        }

        User user = userOptional.get();
        if (!user.getTenantId().equals(tenantId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("User does not belong to current tenant"));
        }

        Role role = roleOptional.get();
        user.removeRole(role);
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    private UserInfoDto convertToDto(User user) {
        return new UserInfoDto(
            user.getUsername(),
            user.getTenantId(),
            user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList())
        );
    }

    private RoleDto convertRoleToDto(Role role) {
        return new RoleDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            null
        );
    }

    // DTOs
    public static class UserInfoDto {
        private String username;
        private String tenantId;
        private List<String> roles;

        public UserInfoDto(String username, String tenantId, List<String> roles) {
            this.username = username;
            this.tenantId = tenantId;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

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
