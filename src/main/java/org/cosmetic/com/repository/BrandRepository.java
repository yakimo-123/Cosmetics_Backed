package org.cosmetic.com.repository;



import org.cosmetic.com.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}