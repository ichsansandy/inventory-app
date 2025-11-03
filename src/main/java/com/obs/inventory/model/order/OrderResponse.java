package com.obs.inventory.model.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderResponse {
    private String orderNo;
    private Long itemId;
    private Long qty;
    private Double price;

    public OrderResponse(String orderNo, Long itemId, Long qty, Double price) {
        this.orderNo = orderNo;
        this.itemId = itemId;
        this.qty = qty;
        this.price = price;
    }
}
