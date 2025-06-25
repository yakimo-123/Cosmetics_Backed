package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.SupplierRequestDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.SupplierMapper;
import org.cosmetic.com.model.Supplier;
import org.cosmetic.com.repository.SupplierRepository;
import org.cosmetic.com.service.SupplierService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier findById(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
    }


    @Override
    public Supplier save(SupplierRequestDto supplier) {
        Supplier supplierEntity = supplierMapper.toEntity(supplier);
        return supplierRepository.save(supplierEntity);
    }

    @Override
    public void deleteById(Long id) {
        supplierRepository.deleteById(id);
    }
}