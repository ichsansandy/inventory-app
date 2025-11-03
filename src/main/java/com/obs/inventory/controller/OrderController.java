package com.obs.inventory.controller;

import com.obs.inventory.model.order.OrderRequest;
import com.obs.inventory.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/pagination")
    public ResponseEntity<?> getPaginationOrder(Pageable pageable){
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetailOrder(@RequestParam String orderNo){
        return ResponseEntity.ok(orderService.getOrderByOrderNo(orderNo));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(req));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editOrder(@Valid @RequestBody OrderRequest request, @RequestParam String orderNo){
        return ResponseEntity.ok(orderService.updateOrder(orderNo,request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteOrder(@RequestParam String orderNo){
        orderService.deleteOrder(orderNo);
        return ResponseEntity.ok("Success Delete Order");
    }
}
