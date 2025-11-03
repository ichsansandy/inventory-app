package com.obs.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Orders {
    @Id
    @Column(name = "order_no", length = 20)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false, name = "qty")
    private Long quantity;

    @Column(nullable = false)
    private Double price;
}
