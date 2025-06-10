package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.SupplierRequestDto;
import org.cosmetic.com.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> findAll();

    Optional<Supplier> findById(Long id);

    Supplier save(SupplierRequestDto supplier);

    void deleteById(Long id);
}