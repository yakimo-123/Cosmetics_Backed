package org.cosmetic.com.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.OrderDetailRequestDto;
import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.enums.PaymentMethod;
import org.cosmetic.com.mapper.OrderDetailMapper;
import org.cosmetic.com.mapper.OrderMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.CartService;
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
    private final CartService cartService;
    private final CartRepository cartRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }


    @Transactional
    @Override
    public Order save(OrderRequestDto requestDto) {
        Order order = orderMapper.toEntity(requestDto);

        // Validate user
        User user = userRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDto.getCustomerId()));
        order.setUser(user);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetailRequestDto> orderDetails = requestDto.getOrderDetails();

        List<Long> productIds = orderDetails.stream()
                .map(OrderDetailRequestDto::getProductId)
                .toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        Map<Long, Inventory> inventoryMap = inventoryRepository.findAllByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));


        for (OrderDetailRequestDto orderDetail : orderDetails) {
            int quantity = orderDetail.getQuantity();
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


            BigDecimal unitPrice = product.getPrice();
            BigDecimal subPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            // Create OrderDetail and set product
            OrderDetail detail = orderDetailMapper.toEntity(orderDetail, product);
            detail.setUnitPrice(unitPrice);
            detail.setSubPrice(subPrice);
            order.addOrderDetail(detail);

            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - quantity);

            // Update total price
            totalPrice = totalPrice.add(subPrice);
        }
        order.setOrderStatus(OrderStatus.PENDING);
        // Set total amount
        order.setTotalAmount(totalPrice);
        // Save order
        order = orderRepository.save(order);
        inventoryRepository.saveAll(inventoryMap.values());
        orderDetailRepository.saveAll(order.getOrderDetails());

        return order;
    }


    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order createOrderFromCart(Long userId, String shippingAddress, PaymentMethod paymentMethod, String sessionId) {

        Cart cart = cartService.getActiveCart(userId, sessionId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for userId: " + userId + " or sessionId: " + sessionId);
        }

        User user = new User();
        user.setId(userId);

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(cart.getTotalAmount())
                .build();
        List<Long> productIds = cart.getCartItems().stream()
                .map(item -> item.getProduct().getId())
                .toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        Map<Long, Inventory> inventoryMap = inventoryRepository.findAllByProductIdIn(productIds).stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));


        cart.getCartItems().forEach(item -> {
            // Validate product
            Product product = productMap.get(item.getProduct().getId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found with id: " + item.getProduct().getId());
            }
            // Validate inventory
            Inventory inventory = inventoryMap.get(product.getId());
            if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient inventory for product: " + product.getProductName());
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subPrice(item.getSubPrice())
                    .build();
            order.addOrderDetail(orderDetail);

            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            // Update total price
        });
        order.setTotalAmount(cart.getTotalAmount());
        // Save order and order details
        Order savedOrder  = orderRepository.save(order);
        orderDetailRepository.saveAll(order.getOrderDetails());

        // Update inventory
        inventoryRepository.saveAll(inventoryMap.values());

        //Complete the cart
        cart.setCartStatus(CartStatus.CONVERTED);
        cartRepository.save(cart);

        return savedOrder;
    }
}