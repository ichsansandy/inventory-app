package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
import com.obs.inventory.entity.Orders;
import com.obs.inventory.exception.ResourceNotFoundException;
import com.obs.inventory.model.order.OrderRequest;
import com.obs.inventory.model.order.OrderResponse;
import com.obs.inventory.repository.OrdersRepository;
import com.obs.inventory.util.OrderIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private InventoryService inventoryService;
    @InjectMocks
    private OrderService orderService;

    private Item item;
    private Orders order;
    private OrderRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item();
        item.setId(1L);
        item.setPrice(5000D);

        order = new Orders();
        order.setOrderNo("O1");
        order.setItem(item);
        order.setQuantity(2L);
        order.setPrice(5000D);

        request = new OrderRequest();
        request.setItemId(1L);
        request.setQuantity(3L);
    }

    // --------------------------
    // getAllOrders
    // --------------------------
    @Test
    void testGetAllOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> mockPage = new PageImpl<>(List.of(new OrderResponse()));
        when(ordersRepository.findAllReturnResponse(pageable)).thenReturn(mockPage);

        Page<OrderResponse> result = orderService.getAllOrders(pageable);

        assertEquals(1, result.getContent().size());
        verify(ordersRepository).findAllReturnResponse(pageable);
    }

    // --------------------------
    // getOrderByOrderNo
    // --------------------------
    @Test
    void testGetOrderByOrderNo_Found() {
        when(ordersRepository.findByOrderNoReturnResponse("O1"))
                .thenReturn(Optional.of(new OrderResponse()));

        OrderResponse result = orderService.getOrderByOrderNo("O1");

        assertNotNull(result);
        verify(ordersRepository).findByOrderNoReturnResponse("O1");
    }

    @Test
    void testGetOrderByOrderNo_NotFound() {
        when(ordersRepository.findByOrderNoReturnResponse("ORD-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderByOrderNo("ORD-999"));
    }

    // --------------------------
    // createOrder
    // --------------------------
    @Test
    void testCreateOrder_Success() {
        when(itemService.getItemByIdForUpdate(1L)).thenReturn(item);
        doNothing().when(inventoryService).isStockEnough(1L, 3L);
        when(ordersRepository.getNextOrderNo()).thenReturn(1L);
        mockStatic(OrderIdGenerator.class).when(() -> OrderIdGenerator.generateOrderId(1L))
                .thenReturn("O1");
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse result = orderService.createOrder(request);

        assertEquals(1L, result.getItemId());
        assertEquals(3L, result.getQty());
        assertEquals(5000D, result.getPrice());
        verify(inventoryService).isStockEnough(1L, 3L);
        verify(ordersRepository).save(any(Orders.class));
    }

    // --------------------------
    // updateOrder
    // --------------------------
    @Test
    void testUpdateOrder_SameItem_IncreaseQuantity() {
        when(ordersRepository.findById("O1")).thenReturn(Optional.of(order));
        when(itemService.getItemByIdForUpdate(1L)).thenReturn(item);
        doNothing().when(inventoryService).isStockEnough(1L, 1L); // newQty - oldQty = 1
        when(ordersRepository.save(any(Orders.class))).thenReturn(order);

        request.setQuantity(3L); // increase qty
        request.setItemId(1L);

        OrderResponse result = orderService.updateOrder("O1", request);

        assertNotNull(result);
        verify(inventoryService).isStockEnough(1L, 1L);
        verify(ordersRepository).save(order);
    }

    @Test
    void testUpdateOrder_DifferentItem() {
        Item newItem = new Item();
        newItem.setId(2L);
        newItem.setPrice(6000D);

        when(ordersRepository.findById("O1")).thenReturn(Optional.of(order));
        when(itemService.getItemByIdForUpdate(2L)).thenReturn(newItem);
        doNothing().when(inventoryService).isStockEnough(2L, 3L);
        when(ordersRepository.save(any(Orders.class))).thenReturn(order);

        request.setItemId(2L);
        request.setQuantity(3L);

        OrderResponse result = orderService.updateOrder("O1", request);

        assertNotNull(result);
        verify(inventoryService).isStockEnough(2L, 3L);
    }

    @Test
    void testUpdateOrder_NotFound() {
        when(ordersRepository.findById("ORD-404")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder("ORD-404", request));
    }

    // --------------------------
    // deleteOrder
    // --------------------------
    @Test
    void testDeleteOrder_Success() {
        when(ordersRepository.findById("O1")).thenReturn(Optional.of(order));
        when(itemService.getItemByIdForUpdate(1L)).thenReturn(item);
        doNothing().when(ordersRepository).deleteById("O1");

        orderService.deleteOrder("O1");

        verify(itemService).getItemByIdForUpdate(1L);
        verify(ordersRepository).deleteById("O1");
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(ordersRepository.findById("ORD-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder("ORD-999"));
    }
}