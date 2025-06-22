package org.cosmetic.com.controller;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.SupplierRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.SupplierResponseDto;
import org.cosmetic.com.mapper.SupplierMapper;
import org.cosmetic.com.model.Supplier;
import org.cosmetic.com.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;


    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponseDto>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.findAll();
        ApiResponse<List<SupplierResponseDto>> response = ApiResponse.<List<SupplierResponseDto>>builder()
                .status(true)
                .message("Suppliers fetched successfully")
                .data(suppliers.stream().map(supplierMapper::toDto).toList())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id);
        ApiResponse<Supplier> response = ApiResponse.<Supplier>builder()
                .status(true)
                .message("Supplier found")
                .data(supplier)
                .build();
        return ResponseEntity.ok(response);
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