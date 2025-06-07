package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(ProductRequestDto product);

    void deleteById(Long id);

    Page<Product> findAll(Pageable pageable);
}