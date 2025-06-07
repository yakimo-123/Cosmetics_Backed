package org.cosmetic.com.service;

import org.cosmetic.com.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryService {
    List<Inventory> findAll();

    Optional<Inventory> findById(Long id);

    Inventory save(Inventory inventory);

    void deleteById(Long id);
}