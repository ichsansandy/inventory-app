package com.obs.inventory.model.item;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemResponse {
    private Long id;
    private String name;
    private Double price;
    private Long stock;

    public ItemResponse(Long id, String name, Double price, Long stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }


    public ItemResponse(Long id, String name, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock.longValue();
    }
}
