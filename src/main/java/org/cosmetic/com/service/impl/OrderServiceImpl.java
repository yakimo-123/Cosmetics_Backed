package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.OrderDetailRequestDto;
import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.mapper.OrderDetailMapper;
import org.cosmetic.com.mapper.OrderMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final InventoryRepository inventoryRepository;

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

        List<Long> productIds = orderDetails.stream()
                .map(OrderDetailRequestDto::getProductId)
                .toList();
        Map<Long,Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        Map<Long, Inventory> inventoryMap = inventoryRepository.findAllByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));


        for (OrderDetailRequestDto orderDetail : orderDetails) {
            BigDecimal subPrice = BigDecimal.ZERO;
            // Validate product
            Product product = productMap.get(orderDetail.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found with id: " + orderDetail.getProductId());
            }
            // Validate inventory
            Inventory inventory = inventoryMap.get(product.getId());
            if (inventory == null || inventory.getQuantity() < orderDetail.getQuantity()) {
                throw new IllegalArgumentException("Insufficient inventory for product: " + product.getProductName());
            }
            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - orderDetail.getQuantity());

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
        inventoryRepository.saveAll(inventoryMap.values());
        orderDetailRepository.saveAll(order.getOrderDetails());

        return order ;
    }




    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}