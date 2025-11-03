package com.obs.inventory.model.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long itemId;
    private Long quantity;
}
