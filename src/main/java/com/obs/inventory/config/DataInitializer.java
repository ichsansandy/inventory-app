package com.obs.inventory.config;

import com.obs.inventory.entity.Item;
import com.obs.inventory.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final ItemRepository itemRepository;

    @PostConstruct
    public void initializeData() {
        log.info("Starting database initialization...");
        List<Item> items = loadItems();
        log.info("Loaded {} items", items.size());

    }

    private List<Item> loadItems() {
        List<Item> items = new ArrayList<>();

        // Item 1: Pen - $5
        Item pen = new Item();
        pen.setName("Pen");
        pen.setPrice(5.0);
        items.add(pen);

        // Item 2: Book - $10
        Item book = new Item();
        book.setName("Book");
        book.setPrice(10.0);
        items.add(book);

        // Item 3: Bag - $30
        Item bag = new Item();
        bag.setName("Bag");
        bag.setPrice(30.0);
        items.add(bag);

        // Item 4: Pencil - $3
        Item pencil = new Item();
        pencil.setName("Pencil");
        pencil.setPrice(3.0);
        items.add(pencil);

        // Item 5: Shoe - $45
        Item shoe = new Item();
        shoe.setName("Shoe");
        shoe.setPrice(45.0);
        items.add(shoe);

        // Item 6: Box - $5
        Item box = new Item();
        box.setName("Box");
        box.setPrice(5.0);
        items.add(box);

        // Item 7: Cap - $25
        Item cap = new Item();
        cap.setName("Cap");
        cap.setPrice(25.0);
        items.add(cap);

        return itemRepository.saveAll(items);
    }
}
