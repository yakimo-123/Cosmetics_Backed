package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.OrderDetailRequestDto;
import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.mapper.OrderDetailMapper;
import org.cosmetic.com.mapper.OrderMapper;
import org.cosmetic.com.model.Order;
import org.cosmetic.com.model.OrderDetail;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.OrderDetailRepository;
import org.cosmetic.com.repository.OrderRepository;
import org.cosmetic.com.repository.ProductRepository;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order save(OrderRequestDto requestDto) {
        Order order = orderMapper.toEntity(requestDto);

        // Validate user
        User user =  userRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDto.getCustomerId()));
        order.setUser(user);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetailRequestDto> orderDetails = requestDto.getOrderDetails();
        for (OrderDetailRequestDto orderDetail : orderDetails) {
            BigDecimal subPrice = BigDecimal.ZERO;
            // Validate product
            Product product = productRepository.findById(orderDetail.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + orderDetail.getProductId()));
            // Create OrderDetail and set product
            subPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
            OrderDetail detail = orderDetailMapper.toEntity(orderDetail, product);
            detail.setUnitPrice(product.getPrice());
            detail.setSubPrice(subPrice);
            order.addOrderDetail(detail);

            // Update total price
            totalPrice = totalPrice.add(subPrice);
        }
        order.setOrderStatus(OrderStatus.PENDING);
        // Set total amount
        order.setTotalAmount(totalPrice);
        // Save order
        order = orderRepository.save(order);
        // Save order details
        orderDetailRepository.saveAll(order.getOrderDetails());

        return order ;
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}