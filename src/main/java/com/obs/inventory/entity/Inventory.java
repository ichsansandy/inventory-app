package com.obs.inventory.entity;

import com.obs.inventory.InventoryActionType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qty")
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private InventoryActionType type;
}
