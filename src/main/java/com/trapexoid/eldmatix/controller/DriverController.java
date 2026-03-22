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

    @GetMapping("/{referenceId}")
    public DriverDto getByReferenceId(@PathVariable String referenceId) {
        if (!guard.allow("driver", "read")) {
            throw new AccessDeniedException("Not allowed to read drivers");
        }
        Driver driver = driverRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return convertToDto(driver);
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

    @PutMapping("/{referenceId}")
    public DriverDto update(@PathVariable String referenceId, @RequestBody DriverDto dto) {
        if (!guard.allow("driver", "write")) {
            throw new AccessDeniedException("Not allowed to write drivers");
        }
        Driver driver = driverRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setFirstName(dto.firstName());
        driver.setLastName(dto.lastName());
        driver.setEmail(dto.email());
        driver.setPhone(dto.phone());
        driver.setDateOfBirth(dto.dateOfBirth());
        driver.setLicenseNumber(dto.licenseNumber());
        driver.setLicenseState(dto.licenseState());
        driver.setLicenseClass(dto.licenseClass());
        driver.setLicenseExpiry(dto.licenseExpiry());

        Driver updated = driverRepository.save(driver);
        return convertToDto(updated);
    }

    @PatchMapping("/{referenceId}/status")
    public DriverDto updateStatus(@PathVariable String referenceId, @RequestParam boolean active) {
        if (!guard.allow("driver", "write")) {
            throw new AccessDeniedException("Not allowed to write drivers");
        }
        Driver driver = driverRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setActive(active);
        Driver updated = driverRepository.save(driver);
        return convertToDto(updated);
    }

    private DriverDto convertToDto(Driver driver) {
        return new DriverDto(
                driver.getId(),
                driver.getReferenceId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getDateOfBirth(),
                driver.getLicenseNumber(),
                driver.getLicenseState(),
                driver.getLicenseClass(),
                driver.getLicenseExpiry(),
                driver.isActive());
    }
}
