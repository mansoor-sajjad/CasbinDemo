package com.trapexoid.eldmatix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trapexoid.eldmatix.model.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
