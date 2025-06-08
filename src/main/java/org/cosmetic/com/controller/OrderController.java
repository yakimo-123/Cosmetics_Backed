package org.cosmetic.com.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.OrderResponseDto;
import org.cosmetic.com.mapper.OrderMapper;
import org.cosmetic.com.model.Order;
import org.cosmetic.com.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
        ApiResponse<List<OrderResponseDto>> response = ApiResponse.<List<OrderResponseDto>>builder()
                .status(true)
                .message("Orders retrieved successfully")
                .data(orders)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> {
                    ApiResponse<OrderResponseDto> response = ApiResponse.<OrderResponseDto>builder()
                            .status(true)
                            .message("Order retrieved successfully")
                            .data(orderMapper.toDto(order))
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto
    ) {
        Order order = orderService.save(requestDto);
        OrderResponseDto orderResponse = orderMapper.toDto(order);
        ApiResponse<OrderResponseDto> response = ApiResponse.<OrderResponseDto>builder()
                .status(true)
                .message("Order created successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(true)
                .message("Order deleted successfully")
                .data("Order with ID " + id + " deleted")
                .build();
        return ResponseEntity.ok(response);
    }
}