package com.trapexoid.eldmatix.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class RoleDto {
    private Integer id;

    @NotBlank(message = "Role name is required")
    private String name;

    private String description;

    private Set<Integer> permissionIds;

    public RoleDto() {
    }

    public RoleDto(Integer id, String name, String description, Set<Integer> permissionIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Integer> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
