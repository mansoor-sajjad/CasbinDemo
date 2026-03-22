package com.trapexoid.eldmatix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trapexoid.eldmatix.model.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
