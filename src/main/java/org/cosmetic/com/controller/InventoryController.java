package org.cosmetic.com.controller;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.model.Inventory;
import org.cosmetic.com.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventories")
public class InventoryController {


    private final InventoryService inventoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventories() {
        List<Inventory> inventories = inventoryService.findAll();
        ApiResponse<List<Inventory>> response = ApiResponse.<List<Inventory>>builder()
                .status(true)
                .message("Inventories fetched successfully")
                .data(inventories)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.findById(id);
        if (inventory.isPresent()) {
            ApiResponse<Inventory> response = ApiResponse.<Inventory>builder()
                    .status(true)
                    .message("Inventory found")
                    .data(inventory.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Inventory> response = ApiResponse.<Inventory>builder()
                    .status(false)
                    .message("Inventory not found")
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@RequestBody Inventory inventory) {
        Inventory savedInventory = inventoryService.save(inventory);
        ApiResponse<Inventory> response = ApiResponse.<Inventory>builder()
                .status(true)
                .message("Inventory created successfully")
                .data(savedInventory)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        Optional<Inventory> existing = inventoryService.findById(id);
        if (existing.isPresent()) {
            inventory.setId(id);
            Inventory updated = inventoryService.save(inventory);
            ApiResponse<Inventory> response = ApiResponse.<Inventory>builder()
                    .status(true)
                    .message("Inventory updated successfully")
                    .data(updated)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Inventory> response = ApiResponse.<Inventory>builder()
                    .status(false)
                    .message("Inventory not found")
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Inventory deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}