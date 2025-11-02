package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
import com.obs.inventory.exception.BadRequestException;
import com.obs.inventory.exception.ResourceNotFoundException;
import com.obs.inventory.model.item.ItemResponse;
import com.obs.inventory.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<ItemResponse> getAllItemsWithStock(Pageable pageable) {
        return itemRepository.findAllWithStock(pageable);
    }

    public ItemResponse getItemByIdWithStock(Long id) {
        return itemRepository.findByIdWithStock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    public void isNameExist(String name) {
        if ( itemRepository.findByName(name).isPresent()) throw new BadRequestException("Item already exist with name "+ name);

    }

    public Item getItemByIdForUpdate(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    public Item createItem(Item item) {
        isNameExist(item.getName());
        return itemRepository.save(item);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Item updateItem(Long id, Item itemDetails) {
        Item item = getItemById(id);
        if (!item.getName().equals(itemDetails.getName())) isNameExist(itemDetails.getName());
        item.setName(itemDetails.getName());
        item.setPrice(itemDetails.getPrice());
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = getItemById(id);
        itemRepository.delete(item);
    }

}
