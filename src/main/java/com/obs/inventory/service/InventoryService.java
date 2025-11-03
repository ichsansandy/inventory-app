package com.obs.inventory.service;

import com.obs.inventory.entity.Inventory;
import com.obs.inventory.entity.Item;
import com.obs.inventory.exception.BadRequestException;
import com.obs.inventory.exception.ResourceNotFoundException;
import com.obs.inventory.model.enums.InventoryActionType;
import com.obs.inventory.model.inventory.InventoryAddRequest;
import com.obs.inventory.model.inventory.InventoryEditRequest;
import com.obs.inventory.model.inventory.InventoryResponse;
import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final OrdersRepository ordersRepository;
    private final ItemService itemService;

    public Page<InventoryResponse> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAllReturnResponse(pageable);
    }

    public InventoryResponse getInventoryByIdReturnResponse(Long id) {
        return inventoryRepository.findByIdReturnResponse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public InventoryResponse createInventory(InventoryAddRequest req) {
        Item item = itemService.getItemByIdForUpdate(req.getItemId());

        if (req.getType().equals(InventoryActionType.W)){
            isStockEnough(req.getItemId(), req.getQty());
        }

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(req.getQty());
        inventory.setType(req.getType());

        return convertToResponse(inventoryRepository.save(inventory));
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public InventoryResponse updateInventory(Long id, InventoryEditRequest req) {
        Inventory inventory = getInventoryForUpdate(id);
        Item item = itemService.getItemByIdForUpdate(req.getItemId());
        InventoryActionType oldType = inventory.getType();
        InventoryActionType newType = req.getType();

        boolean becomingWithdrawal = !oldType.equals(InventoryActionType.W) && newType.equals(InventoryActionType.W);
        boolean increasingWithdrawal = oldType.equals(InventoryActionType.W) && newType.equals(InventoryActionType.W)
                && req.getQty() > inventory.getQuantity();

        if (becomingWithdrawal || increasingWithdrawal) {
            isStockEnough(req.getItemId(), req.getQty() - inventory.getQuantity());
        }

        inventory.setItem(item);
        inventory.setQuantity(req.getQty());
        inventory.setType(newType);
        return convertToResponse(inventoryRepository.save(inventory));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteInventory(Long id) {
        inventoryRepository.delete(getInventoryForUpdate(id));
    }

    public Inventory getInventoryForUpdate(Long id){
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    public Long calculateStock(Long itemId){
        Long inventoryStock = inventoryRepository.getCurrentStock(itemId);
        Long orderedQty = ordersRepository.getTotalOrderedQty(itemId);
        return inventoryStock - orderedQty;
    }

    public void isStockEnough(Long itemId, Long qty){
        Long stock = calculateStock(itemId);
        if (stock < qty){
            throw new BadRequestException("Stock not enough");
        }
    }

    private InventoryResponse convertToResponse(Inventory inventory){
        return new InventoryResponse(
                inventory.getId(),
                inventory.getItem().getId(),
                inventory.getQuantity(),
                inventory.getType()
        );
    }
}
