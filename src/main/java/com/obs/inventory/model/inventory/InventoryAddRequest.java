package com.obs.inventory.model.inventory;

import com.obs.inventory.model.enums.InventoryActionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAddRequest {
    @NotNull
    private Long itemId;
    @NotNull
    @Min(1)
    private Long qty;
    private InventoryActionType type;
}
