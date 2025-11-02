package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
import com.obs.inventory.repository.ItemRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    public boolean isNameExist(String name) {
        return itemRepository.findByName(name).isPresent();

    }

    @Transactional
    public Item createItem(Item item) {
        if (isNameExist(item.getName())) throw new BadRequestException("Item already exist with nane "+ item.getName());
        return itemRepository.save(item);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Item updateItem(Long id, Item itemDetails) {
        Item item = getItemById(id);

        if (isNameExist(item.getName())) throw new BadRequestException("Item already exist with nane "+ item.getName());
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
