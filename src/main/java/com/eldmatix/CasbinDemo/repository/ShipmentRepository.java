package com.eldmatix.CasbinDemo.repository;

import com.eldmatix.CasbinDemo.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
