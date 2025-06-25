package org.cosmetic.com.repository;

import org.cosmetic.com.enums.ProductStatus;
import org.cosmetic.com.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductStatusNotIn(Collection<ProductStatus> productStatus, Pageable pageable);

}