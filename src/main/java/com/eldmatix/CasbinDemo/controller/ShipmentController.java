package com.eldmatix.CasbinDemo.controller;

import com.eldmatix.CasbinDemo.dto.ShipmentDto;
import com.eldmatix.CasbinDemo.model.Shipment;
import com.eldmatix.CasbinDemo.repository.ShipmentRepository;
import com.eldmatix.CasbinDemo.security.AuthorizationGuard;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final AuthorizationGuard guard;

    public ShipmentController(ShipmentRepository shipmentRepository, AuthorizationGuard guard) {
        this.shipmentRepository = shipmentRepository;
        this.guard = guard;
    }

    @GetMapping
    public List<ShipmentDto> getAll() {
        if (!guard.allow("shipment", "read")) {
            throw new AccessDeniedException("Not allowed to read shipments");
        }
        return shipmentRepository.findAll().stream()
                .map(s -> new ShipmentDto(s.getId(), s.getDescription()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ShipmentDto create(@RequestBody ShipmentDto dto) {
        if (!guard.allow("shipment", "write")) {
            throw new AccessDeniedException("Not allowed to write shipments");
        }
        Shipment shipment = new Shipment(dto.description());
        Shipment saved = shipmentRepository.save(shipment);
        return new ShipmentDto(saved.getId(), saved.getDescription());
    }

    // Java 25 Pattern Matching Example (Switch with pattern matching)
    // This is just a demonstration method
    public String determineAccessLevel(Object roleObject) {
        return switch (roleObject) {
            case String role when role.equalsIgnoreCase("ADMIN") -> "Full Access";
            case String role when role.startsWith("USER") -> "Restricted Access";
            case Integer level when level > 5 -> "High Level Access";
            case null -> "No Access";
            default -> "Unknown Access";
        };
    }
}
