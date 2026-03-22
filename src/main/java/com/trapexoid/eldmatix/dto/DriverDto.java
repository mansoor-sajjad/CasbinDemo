package com.trapexoid.eldmatix.dto;

import java.time.LocalDate;

public record DriverDto(
                Long id,
                String referenceId,
                String firstName,
                String lastName,
                String email,
                String phone,
                LocalDate dateOfBirth,
                String licenseNumber,
                String licenseState,
                String licenseClass,
                LocalDate licenseExpiry,
                boolean isActive) {
}
