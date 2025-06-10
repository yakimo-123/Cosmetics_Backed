package org.cosmetic.com.controller;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.SupplierRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.model.Supplier;
import org.cosmetic.com.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        ApiResponse<List<Supplier>> response = ApiResponse.<List<Supplier>>builder()
                .status(true)
                .message("Suppliers fetched successfully")
                .data(suppliers)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierService.findById(id);
        if (supplier.isPresent()) {
            ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                    .status(true)
                    .message("Supplier found")
                    .data(supplier.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                    .status(false)
                    .message("Supplier not found")
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@RequestBody SupplierRequestDto requestDto) {
        Supplier savedSupplier = supplierService.save(requestDto);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .status(true)
                .message("Supplier created successfully")
                .data(savedSupplier)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(true)
                .message("Supplier deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}