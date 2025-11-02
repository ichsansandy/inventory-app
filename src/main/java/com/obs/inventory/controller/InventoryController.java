package com.obs.inventory.controller;

import com.obs.inventory.model.inventory.InventoryAddRequest;
import com.obs.inventory.model.inventory.InventoryEditRequest;
import com.obs.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/pagination")
    public ResponseEntity<?> getPaginationInventory(Pageable pageable){
        return ResponseEntity.ok(inventoryService.getAllInventory(pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetailInventory(@RequestParam Long id){
        return ResponseEntity.ok(inventoryService.getInventoryByIdReturnResponse(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addInventory(@Valid @RequestBody InventoryAddRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(req));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editInventory(@Valid @RequestBody InventoryEditRequest request, @RequestParam Long id){
        return ResponseEntity.ok(inventoryService.updateInventory(id,request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteInventory(@RequestParam Long id){
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok("Success Delete Inventory");
    }
}
