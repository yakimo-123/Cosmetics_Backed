// src/main/java/org/cosmetic/com/repository/CategoryRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}