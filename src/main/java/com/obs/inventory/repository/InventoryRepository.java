package com.obs.inventory.repository;

import com.obs.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
