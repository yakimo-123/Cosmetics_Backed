// src/main/java/org/cosmetic/com/repository/OrderDetailRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}