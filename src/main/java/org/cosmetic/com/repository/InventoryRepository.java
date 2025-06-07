// src/main/java/org/cosmetic/com/repository/InventoryRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}