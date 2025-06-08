package org.cosmetic.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant orderDate;


    private BigDecimal totalAmount;

    @Column(name = "order_status", nullable = false)
    private OrderStatus OrderStatus;


    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @PrePersist
    protected void onCreate() {
        this.orderDate = Instant.now();
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        if (orderDetails == null) {
            orderDetails = new ArrayList<>();
        }
        if (orderDetail == null) {
            throw new IllegalStateException("Order details cannot be null");
        }
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

}