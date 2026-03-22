package com.trapexoid.eldmatix.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.trapexoid.eldmatix.dto.DriverDto;
import com.trapexoid.eldmatix.model.Driver;
import com.trapexoid.eldmatix.repository.DriverRepository;
import com.trapexoid.eldmatix.security.AuthorizationGuard;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final AuthorizationGuard guard;

    public DriverController(DriverRepository driverRepository, AuthorizationGuard guard) {
        this.driverRepository = driverRepository;
        this.guard = guard;
    }

    @GetMapping
    public List<DriverDto> getAll() {
        if (!guard.allow("driver", "read")) {
            throw new AccessDeniedException("Not allowed to read drivers");
        }
        return driverRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public DriverDto create(@RequestBody DriverDto dto) {
        if (!guard.allow("driver", "write")) {
            throw new AccessDeniedException("Not allowed to write drivers");
        }
        Driver driver = new Driver(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.phone(),
                dto.dateOfBirth(),
                dto.licenseNumber(),
                dto.licenseState(),
                dto.licenseClass(),
                dto.licenseExpiry());
        Driver saved = driverRepository.save(driver);
        return convertToDto(saved);
    }

    private DriverDto convertToDto(Driver driver) {
        return new DriverDto(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getDateOfBirth(),
                driver.getLicenseNumber(),
                driver.getLicenseState(),
                driver.getLicenseClass(),
                driver.getLicenseExpiry());
    }
}
