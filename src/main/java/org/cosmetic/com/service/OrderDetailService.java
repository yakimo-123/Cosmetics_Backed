package org.cosmetic.com.service;

import org.cosmetic.com.model.OrderDetail;
import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetail> findAll();
    Optional<OrderDetail> findById(Long id);
    OrderDetail save(OrderDetail orderDetail);
    void deleteById(Long id);
}