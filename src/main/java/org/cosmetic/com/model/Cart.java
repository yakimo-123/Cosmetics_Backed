package org.cosmetic.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cosmetic.com.enums.CartStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_id")
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void addCartItem(CartItem newItem) {
        if(cartItems == null) {
            cartItems = new ArrayList<>();
        }
        for (CartItem existingItem : cartItems) {
            if (existingItem.getProduct().getId().equals(newItem.getProduct().getId())) {
                // Nếu đã có sản phẩm → tăng số lượng và cập nhật subPrice
                int updatedQuantity = existingItem.getQuantity() + newItem.getQuantity();
                existingItem.setQuantity(updatedQuantity);
                existingItem.updateSubPrice();
                updateTotalAmount();
                return;
            }
        }

        // Nếu chưa có → thêm mới
        newItem.setCart(this);
        newItem.updateSubPrice();
        cartItems.add(newItem);
        updateTotalAmount();
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        updateTotalAmount();
    }

    @PreUpdate
    public void updateTotalAmount() {
        totalAmount = cartItems.stream()
                .map(CartItem::getSubPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}