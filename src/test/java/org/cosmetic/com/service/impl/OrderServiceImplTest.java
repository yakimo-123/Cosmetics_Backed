package org.cosmetic.com.service.impl;

import org.cosmetic.com.dto.request.OrderDetailRequestDto;
import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.enums.PaymentMethod;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.OrderDetailMapper;
import org.cosmetic.com.mapper.OrderMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderDetailMapper orderDetailMapper;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private CartService cartService;
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    @DisplayName("Find All Orders Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all orders when they exist")
        void shouldReturnAllOrdersWhenTheyExist() {
            // Given
            List<Order> expectedOrders = Arrays.asList(
                createOrder(1L),
                createOrder(2L)
            );
            when(orderRepository.findAll()).thenReturn(expectedOrders);

            // When
            List<Order> actualOrders = orderService.findAll();

            // Then
            assertNotNull(actualOrders);
            assertEquals(2, actualOrders.size());
            verify(orderRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no orders exist")
        void shouldReturnEmptyListWhenNoOrdersExist() {
            // Given
            when(orderRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<Order> actualOrders = orderService.findAll();

            // Then
            assertTrue(actualOrders.isEmpty());
            verify(orderRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Find Order By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return order when it exists")
        void shouldReturnOrderWhenItExists() {
            // Given
            Long orderId = 1L;
            Order expectedOrder = createOrder(orderId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

            // When
            Optional<Order> actualOrder = orderService.findById(orderId);

            // Then
            assertTrue(actualOrder.isPresent());
            assertEquals(orderId, actualOrder.get().getId());
            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("Should return empty when order doesn't exist")
        void shouldReturnEmptyWhenOrderDoesntExist() {
            // Given
            Long orderId = 1L;
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            Optional<Order> actualOrder = orderService.findById(orderId);

            // Then
            assertFalse(actualOrder.isPresent());
            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("Save Order Tests")
    class SaveOrderTests {

        @Test
        @DisplayName("Should successfully save order with valid data")
        void shouldSuccessfullySaveOrderWithValidData() {
            // Given
            OrderRequestDto requestDto = createOrderRequestDto();
            Order order = createOrder(1L);
            User user = createUser(1L);
            Product product = createProduct(1L);
            Inventory inventory = createInventory(1L, product, 10);
            OrderDetail orderDetail = createOrderDetail(1L, product);

            when(orderMapper.toEntity(requestDto)).thenReturn(order);
            when(userRepository.findById(requestDto.getCustomerId())).thenReturn(Optional.of(user));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(inventoryRepository.findAllByProductIdIn(anyList())).thenReturn(List.of(inventory));
            when(orderDetailMapper.toEntity(any(), any())).thenReturn(orderDetail);
            when(orderRepository.save(any())).thenReturn(order);

            // When
            Order savedOrder = orderService.save(requestDto);


            // Then
            assertNotNull(savedOrder);
            verify(orderRepository).save(any(Order.class));
            verify(orderDetailRepository).saveAll(anyList());

            // ✅ Kiểm tra inventory đã bị cập nhật tồn kho
            ArgumentCaptor<Collection<Inventory>> captor = ArgumentCaptor.forClass(Collection.class);
            verify(inventoryRepository).saveAll(captor.capture());

            Collection<Inventory> savedInventories = captor.getValue();
            assertEquals(1, savedInventories.size());
            Inventory updated = savedInventories.iterator().next();

            assertEquals(product.getId(), updated.getProduct().getId());
            assertEquals(5, updated.getQuantity()); // ban đầu 10 - đặt 5 = còn 5
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            OrderRequestDto requestDto = createOrderRequestDto();
            Order order = createOrder(1L);
            when(orderMapper.toEntity(requestDto)).thenReturn(order);
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> orderService.save(requestDto));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when insufficient inventory")
        void shouldThrowExceptionWhenInsufficientInventory() {
            // Given
            OrderRequestDto requestDto = createOrderRequestDto();
            Order order = createOrder(1L);
            User user = createUser(1L);
            Product product = createProduct(1L);
            Inventory inventory = createInventory(1L, product, 1); // Only 1 in stock

            when(orderMapper.toEntity(requestDto)).thenReturn(order);
            when(userRepository.findById(any())).thenReturn(Optional.of(user));
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(inventoryRepository.findAllByProductIdIn(anyList())).thenReturn(List.of(inventory));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> orderService.save(requestDto));
            assertEquals(ErrorCode.INSUFFICIENT_INVENTORY, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Create Order From Cart Tests")
    class CreateOrderFromCartTests {

        @Test
        @DisplayName("Should successfully create order from cart")
        void shouldSuccessfullyCreateOrderFromCart() {
            // Given
            Long userId = 1L;
            String shippingAddress = "Test Address";
            PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
            String sessionId = "test-session";

            Cart cart = createCart(userId);
            Product product = createProduct(1L);
            Inventory inventory = createInventory(1L, product, 10);
            CartItem cartItem = createCartItem(cart, product, 2);
            cart.setCartItems(List.of(cartItem));

            when(cartService.getActiveCart(userId)).thenReturn(cart);
            when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
            when(inventoryRepository.findAllByProductIdIn(anyList())).thenReturn(List.of(inventory));
            when(orderRepository.save(any())).thenReturn(createOrder(1L));

            // When
            Order createdOrder = orderService.createOrderFromCart(userId, shippingAddress, 
                paymentMethod);

            // Then
            assertNotNull(createdOrder);
            verify(orderRepository).save(any(Order.class));
            verify(orderDetailRepository).saveAll(anyList());
            verify(cartRepository).save(any(Cart.class));

            ArgumentCaptor<Collection<Inventory>> captor =
                    ArgumentCaptor.forClass(Collection.class);
            verify(inventoryRepository).saveAll(captor.capture());

            Collection<Inventory> captured = captor.getValue();
            assertEquals(1, captured.size());

            Inventory updated = captured.iterator().next();
            assertEquals(product.getId(), updated.getProduct().getId());
            assertEquals(8, updated.getQuantity()); // ví dụ ban đầu 10 - mua 2 = còn 8

        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            when(cartService.getActiveCart(any())).thenReturn(null);

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> orderService.createOrderFromCart(1L, "address", PaymentMethod.CASH_ON_DELIVERY));
            assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
        }
    }

    // Helper methods to create test objects
    private Order createOrder(Long id) {
        return Order.builder()
                .id(id)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100))
                .build();
    }

    private OrderRequestDto createOrderRequestDto() {
        OrderDetailRequestDto detailDto = new OrderDetailRequestDto();
        detailDto.setProductId(1L);
        detailDto.setQuantity(5);

        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1L);
        dto.setOrderDetails(List.of(detailDto));
        return dto;
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .username("testuser")
                .build();
    }

    private Product createProduct(Long id) {
        return Product.builder()
                .id(id)
                .productName("Test Product")
                .price(BigDecimal.valueOf(10))
                .build();
    }

    private Inventory createInventory(Long id, Product product, int quantity) {
        return Inventory.builder()
                .id(id)
                .product(product)
                .quantity(quantity)
                .build();
    }

    private OrderDetail createOrderDetail(Long id, Product product) {
        return OrderDetail.builder()
                .id(id)
                .product(product)
                .quantity(5)
                .unitPrice(product.getPrice())
                .subPrice(product.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();
    }

    private Cart createCart(Long userId) {
        return Cart.builder()
                .id(1L)
                .user(createUser(userId))
                .cartStatus(CartStatus.ACTIVE)
                .totalAmount(BigDecimal.valueOf(100))
                .build();
    }

    private CartItem createCartItem(Cart cart, Product product, int quantity) {
        return CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .subPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }
}