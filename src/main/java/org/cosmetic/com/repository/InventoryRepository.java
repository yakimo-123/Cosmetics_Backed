// src/main/java/org/cosmetic/com/repository/InventoryRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Inventory;
import org.cosmetic.com.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);
    List<Inventory> findAllByProductIdIn(List<Long> product_id);
}