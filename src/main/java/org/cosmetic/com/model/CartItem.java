package org.cosmetic.com.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal price = BigDecimal.ZERO;

    private BigDecimal subPrice = BigDecimal.ZERO;

    private BigDecimal unitPrice = BigDecimal.ZERO;


    public void updateSubPrice() {
        if (product != null && price != null && quantity != null) {
            this.subPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subPrice = BigDecimal.ZERO;
        }
    }


}