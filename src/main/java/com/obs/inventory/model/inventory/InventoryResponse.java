package com.obs.inventory.model.inventory;

import com.obs.inventory.model.enums.InventoryActionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryResponse {
    private Long id;
    private Long item_id;
    private Long qty;
    private InventoryActionType type;

    public InventoryResponse(Long id, Long item_id, Long qty, InventoryActionType type) {
        this.id = id;
        this.item_id = item_id;
        this.qty = qty;
        this.type = type;
    }
}
