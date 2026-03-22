package com.trapexoid.eldmatix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trapexoid.eldmatix.model.Driver;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByReferenceId(String referenceId);
}
