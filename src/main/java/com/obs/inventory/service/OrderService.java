package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
import com.obs.inventory.entity.Orders;
import com.obs.inventory.exception.ResourceNotFoundException;
import com.obs.inventory.model.order.OrderRequest;
import com.obs.inventory.model.order.OrderResponse;
import com.obs.inventory.repository.OrdersRepository;
import com.obs.inventory.util.OrderIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final ItemService itemService;
    private final InventoryService inventoryService;

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return ordersRepository.findAllReturnResponse(pageable);
    }

    public OrderResponse getOrderByOrderNo(String orderNo) {
        return ordersRepository.findByOrderNoReturnResponse(orderNo).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderResponse createOrder(OrderRequest request) {
        Item item = itemService.getItemByIdForUpdate(request.getItemId());
        inventoryService.isStockEnough(item.getId(), request.getQuantity());

        Orders order = new Orders();
        order.setItem(item);
        order.setQuantity(request.getQuantity());
        order.setPrice(item.getPrice());
        order.setOrderNo(OrderIdGenerator.generateOrderId(ordersRepository.getNextOrderNo()));

        return toResponse(ordersRepository.save(order));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderResponse updateOrder(String orderNo, OrderRequest request) {
        Orders order = ordersRepository.findById(orderNo).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Item newItem = itemService.getItemByIdForUpdate(request.getItemId());
        Item oldItem = order.getItem();

        Long oldQty = order.getQuantity();
        Long newQty = request.getQuantity();

        // --- CASE 1: same item ---
        if (oldItem.getId().equals(newItem.getId())) {
            if (newQty > oldQty) {
                Long additionalQty = newQty - oldQty;
                inventoryService.isStockEnough(newItem.getId(), additionalQty);
            }
        }
        else {
            // check if new item has enough stock for full new quantity
            inventoryService.isStockEnough(newItem.getId(), newQty);
        }

        order.setItem(newItem);
        order.setQuantity(newQty);
        order.setPrice(newItem.getPrice());
        return toResponse(ordersRepository.save(order));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteOrder(String orderNo) {
        Orders order = ordersRepository.findById(orderNo).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        itemService.getItemByIdForUpdate(order.getItem().getId());
        ordersRepository.deleteById(orderNo);
    }

    private OrderResponse toResponse(Orders order){
        return OrderResponse.builder()
               .orderNo(order.getOrderNo())
               .itemId(order.getItem().getId())
               .qty(order.getQuantity())
               .price(order.getPrice())
               .build();
    }
}
