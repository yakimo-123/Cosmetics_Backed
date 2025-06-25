package org.cosmetic.com.repository;


import org.cosmetic.com.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Boolean existsByNameIgnoreCase(String name);
}