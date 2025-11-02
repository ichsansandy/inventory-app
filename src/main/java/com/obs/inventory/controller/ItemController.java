package com.obs.inventory.controller;

import com.obs.inventory.entity.Item;
import com.obs.inventory.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/pagination")
    public ResponseEntity<?> getPaginationItem(Pageable pageable){
        return ResponseEntity.ok(itemService.getAllItems(pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getDetailItem(@RequestParam Long id){
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody Item item){
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(item));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editItem(@RequestBody Item item, @RequestParam Long id){
        return ResponseEntity.ok(itemService.updateItem(id,item));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestParam Long id){
        itemService.deleteItem(id);
        return ResponseEntity.ok("Success Delete Item");
    }
}
