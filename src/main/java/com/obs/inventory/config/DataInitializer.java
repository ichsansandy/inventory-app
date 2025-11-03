package com.obs.inventory.config;

import com.obs.inventory.entity.Order;
import com.obs.inventory.model.enums.InventoryActionType;
import com.obs.inventory.entity.Inventory;
import com.obs.inventory.entity.Item;
import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.repository.OrderRepository;
import com.obs.inventory.util.OrderIdGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    @PostConstruct
    public void initializeData() {
        log.info("Starting database initialization...");
        List<Item> items = loadItems();
        log.info("Loaded {} items", items.size());
        List<Inventory> inventory = loadInventory(items);
        log.info("Loaded {} inventory", inventory.size());
        List<Order> orders = loadOrders(items);
        log.info("Loaded {} orders", orders.size());
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

    private List<Inventory> loadInventory(List<Item> items){
        List<Inventory> inventoryList =  new ArrayList<>();

        // item id 1 qty 5 type t
        Inventory one = new Inventory();
        one.setItem(items.get(1-1));
        one.setQuantity(5L);
        one.setType(InventoryActionType.T);
        inventoryList.add(one);
        // item id 2 qty 10 type t
        Inventory two = new Inventory();
        two.setItem(items.get(2-1));
        two.setQuantity(10L);
        two.setType(InventoryActionType.T);
        inventoryList.add(two);
        // item id 3 qty 30 type t
        Inventory three = new Inventory();
        three.setItem(items.get(3-1));
        three.setQuantity(30L);
        three.setType(InventoryActionType.T);
        inventoryList.add(three);
        // item id 4 qty 3 type t
        Inventory four = new Inventory();
        four.setItem(items.get(4-1));
        four.setQuantity(3L);
        four.setType(InventoryActionType.T);
        inventoryList.add(four);
        // item id 5 qty 45 type t
        Inventory five = new Inventory();
        five.setItem(items.get(5-1));
        five.setQuantity(45L);
        five.setType(InventoryActionType.T);
        inventoryList.add(five);
        // item id 6 qty 5 type t
        Inventory six = new Inventory();
        six.setItem(items.get(6-1));
        six.setQuantity(5L);
        six.setType(InventoryActionType.T);
        inventoryList.add(six);
        // item id 7 qty 25 type t
        Inventory seven = new Inventory();
        seven.setItem(items.get(7-1));
        seven.setQuantity(25L);
        seven.setType(InventoryActionType.T);
        inventoryList.add(seven);
        // item id 4 qty 7 type t
        Inventory eight = new Inventory();
        eight.setItem(items.get(4-1));
        eight.setQuantity(7L);
        eight.setType(InventoryActionType.T);
        inventoryList.add(eight);
        // item id 5 qty 10 type w
        Inventory nine = new Inventory();
        nine.setItem(items.get(5-1));
        nine.setQuantity(10L);
        nine.setType(InventoryActionType.W);
        inventoryList.add(nine);

        return inventoryRepository.saveAll(inventoryList);
    }

    private List<Order> loadOrders(List<Item> items){
        List<Order> orders = new ArrayList<>();
        // 1 item 1 qty 2
        Order order = new Order();
        order.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order.setItem(items.get(1-1));
        order.setQuantity(2);
        order.setPrice(items.get(1-1).getPrice());
        orders.add(order);
        orderRepository.save(order);
        // 2 item 2 qty 3
        Order order2 = new Order();
        order2.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order2.setItem(items.get(2-1));
        order2.setQuantity(3);
        order2.setPrice(items.get(2-1).getPrice());
        orders.add(order2);
        // 3 item 5 qty 4
        Order order3 = new Order();
        order3.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order3.setItem(items.get(5-1));
        order3.setQuantity(4);
        order3.setPrice(items.get(5-1).getPrice());
        orders.add(order3);
        // 4 item 4 qty 1
        Order order4 = new Order();
        order4.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order4.setItem(items.get(4-1));
        order4.setQuantity(1);
        order4.setPrice(items.get(4-1).getPrice());
        orders.add(order4);
        // 5 item 5 qty 2
        Order order5 = new Order();
        order5.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order5.setItem(items.get(5-1));
        order5.setQuantity(2);
        order5.setPrice(items.get(5-1).getPrice());
        orders.add(order5);
        // 6 item 6 qty 3
        Order order6 = new Order();
        order6.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order6.setItem(items.get(6-1));
        order6.setQuantity(3);
        order6.setPrice(items.get(6-1).getPrice());
        orders.add(order6);
        // 7 item 1 qty 5
        Order order7 = new Order();
        order7.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order7.setItem(items.get(1-1));
        order7.setQuantity(5);
        order7.setPrice(items.get(1-1).getPrice());
        orders.add(order7);
        // 8 item 2 qty 4
        Order order8 =  new Order();
        order8.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order8.setItem(items.get(2-1));
        order8.setQuantity(4);
        order8.setPrice(items.get(2-1).getPrice());
        orders.add(order8);
        // 9 item 3 qty 2
        Order order9 = new Order();
        order9.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order9.setItem(items.get(3-1));
        order9.setQuantity(2);
        order9.setPrice(items.get(3-1).getPrice());
        orders.add(order9);
        // 10 item 4 qty 3
        Order order10 = new Order();
        order10.setOrderNo(OrderIdGenerator.generateOrderId(orderRepository.getNextOrderNo()));
        order10.setItem(items.get(4-1));
        order10.setQuantity(3);
        order10.setPrice(items.get(4-1).getPrice());
        orders.add(order10);

        return orderRepository.saveAll(orders);
    }
}
