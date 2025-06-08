package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(OrderRequestDto orderRequestDto);

    void deleteById(Long id);
}