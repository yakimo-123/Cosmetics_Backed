// src/main/java/org/cosmetic/com/repository/SupplierRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Supplier getSuppliersById(Long id);

}