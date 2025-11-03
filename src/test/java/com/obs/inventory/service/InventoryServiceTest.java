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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(10L);
        inventory.setType(InventoryActionType.T);

        Item item = new Item();
        item.setId(1L);
        item.setName("Pen");
        item.setPrice(10.0);

        inventory.setItem(item);


        response = new InventoryResponse(1L, 1L, 10L, InventoryActionType.T);
    }

    @Test
    void getAllInventory_ShouldReturnPage() {
        Pageable pageable = mock(Pageable.class);
        when(inventoryRepository.findAllReturnResponse(pageable))
                .thenReturn(new PageImpl<>(List.of(response)));

        Page<InventoryResponse> result = inventoryService.getAllInventory(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(inventoryRepository, times(1)).findAllReturnResponse(pageable);
    }

    @Test
    void getInventoryByIdReturnResponse_WhenFound_ShouldReturnResponse() {
        when(inventoryRepository.findByIdReturnResponse(1L)).thenReturn(Optional.of(response));

        InventoryResponse result = inventoryService.getInventoryByIdReturnResponse(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(inventoryRepository).findByIdReturnResponse(1L);
    }

    @Test
    void getInventoryByIdReturnResponse_WhenNotFound_ShouldThrowException() {
        when(inventoryRepository.findByIdReturnResponse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                inventoryService.getInventoryByIdReturnResponse(1L));
    }

    @Test
    void createInventory_WhenTypeIsWithdrawAndStockEnough_ShouldSaveInventory() {
        InventoryAddRequest req = new InventoryAddRequest();
        req.setItemId(1L);
        req.setQty(5L);
        req.setType(InventoryActionType.W);

        when(inventoryRepository.getCurrentStock(1L)).thenReturn(10L);
        when(itemService.getItemById(1L)).thenReturn(new Item());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryResponse result = inventoryService.createInventory(req);

        assertThat(result.getQty()).isEqualTo(10L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void createInventory_WhenStockNotEnough_ShouldThrowException() {
        InventoryAddRequest req = new InventoryAddRequest();
        req.setItemId(1L);
        req.setQty(15L);
        req.setType(InventoryActionType.W);

        when(inventoryRepository.getCurrentStock(1L)).thenReturn(5L);

        assertThrows(BadRequestException.class, () ->
                inventoryService.createInventory(req));
    }

    @Test
    void updateInventory_WhenBecomesWithdrawalAndStockEnough_ShouldUpdateInventory() {
        InventoryEditRequest req = new InventoryEditRequest();
        req.setQty(10L);
        req.setType(InventoryActionType.W);
        req.setItemId(1L);
        Item item = new Item();
        item.setId(1L);
        inventory.setType(InventoryActionType.T);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.getCurrentStock(1L)).thenReturn(20L);
        when(itemService.getItemByIdForUpdate(1L)).thenReturn(item);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryResponse result = inventoryService.updateInventory(1L, req);

        assertThat(result.getQty()).isEqualTo(10L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void deleteInventory_ShouldDeleteSuccessfully() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        inventoryService.deleteInventory(1L);

        verify(inventoryRepository).delete(inventory);
    }

    @Test
    void isStockEnough_WhenStockLessThanQty_ShouldThrowException() {
        when(inventoryRepository.getCurrentStock(1L)).thenReturn(3L);

        assertThrows(BadRequestException.class, () ->
                inventoryService.isStockEnough(1L, 5L));
    }

    @Test
    void isStockEnough_WhenStockSufficient_ShouldPass() {
        when(inventoryRepository.getCurrentStock(1L)).thenReturn(10L);

        inventoryService.isStockEnough(1L, 5L);

        verify(inventoryRepository).getCurrentStock(1L);
    }

}