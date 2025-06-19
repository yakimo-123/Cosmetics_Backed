package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.model.Inventory;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.repository.InventoryRepository;
import org.cosmetic.com.repository.ProductRepository;
import org.cosmetic.com.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public Inventory getOrCreateInventory(Long productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID must not be null");
        }
        Inventory inventory = inventoryRepository.findByProduct_Id(productId);

        if(inventory != null) {
            inventory.setQuantity(inventory.getQuantity() + quantity);
            return inventoryRepository.save(inventory);
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("Product not found with id: " + productId)
        );

        inventory =Inventory.builder()
                .product(product)
                .quantity(quantity)
                .build();
        return inventoryRepository.save(inventory);
    }
}