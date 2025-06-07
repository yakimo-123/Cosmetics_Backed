// src/main/java/org/cosmetic/com/repository/OrderRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}