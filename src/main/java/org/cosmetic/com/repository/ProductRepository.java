
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}