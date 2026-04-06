package com.trapexoid.eldmatix.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionDto {
    private Integer id;

    @NotBlank(message = "Permission name is required")
    private String name;

    private String description;

    @NotBlank(message = "Resource is required")
    private String resource;

    @NotBlank(message = "Action is required")
    private String action;

    public PermissionDto() {
    }

    public PermissionDto(Integer id, String name, String description, String resource, String action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
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

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
