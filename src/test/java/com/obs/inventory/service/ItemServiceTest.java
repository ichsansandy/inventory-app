package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
import com.obs.inventory.exception.BadRequestException;
import com.obs.inventory.exception.ResourceNotFoundException;
import com.obs.inventory.model.item.ItemResponse;
import com.obs.inventory.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private ItemResponse testItemResponse;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Pen");
        testItem.setPrice(5.0);

        testItemResponse = new ItemResponse(1L, "Shoes", 99.99, 50L);
    }

    @Test
    void getAllItems_ShouldReturnPageOfItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(Arrays.asList(testItem));

        when(itemRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Item> result = itemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(itemRepository, times(1)).findAll(pageable);
    }

    @Test
    void getItemById_WhenItemExists_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        Item result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Pen", result.getName());
        assertEquals(5.0, result.getPrice());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemById_WhenItemDoesNotExist_ShouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(999L));
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    void getAllItemsWithStock_ShouldReturnPagedItems() {
        Pageable pageable = PageRequest.of(0, 10);
        // Arrange
        List<ItemResponse> items = List.of(testItemResponse);
        Page<ItemResponse> itemPage = new PageImpl<>(items, pageable, items.size());
        when(itemRepository.findAllWithStock(pageable)).thenReturn(itemPage);

        // Act
        Page<ItemResponse> result = itemService.getAllItemsWithStock(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Shoes", result.getContent().get(0).getName());
        verify(itemRepository, times(1)).findAllWithStock(pageable);
    }

    @Test
    void getItemByIdWithStock_WhenItemExists_ShouldReturnItemResponse() {
        // Arrange
        when(itemRepository.findByIdWithStock(1L)).thenReturn(Optional.of(testItemResponse));

        // Act
        ItemResponse result = itemService.getItemByIdWithStock(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Shoes", result.getName());
        verify(itemRepository, times(1)).findByIdWithStock(1L);
    }

    @Test
    void createItem_ShouldSaveAndReturnItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        Item result = itemService.createItem(testItem);

        assertNotNull(result);
        assertEquals("Pen", result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_WhenItemExists_ShouldUpdateAndReturnItem() {
        Item updatedDetails = new Item();
        updatedDetails.setName("Updated Pen");
        updatedDetails.setPrice(10.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        Item result = itemService.updateItem(1L, updatedDetails);

        assertNotNull(result);
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void deleteItem_WhenItemExists_ShouldDeleteItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(itemRepository).delete(testItem);

        itemService.deleteItem(1L);

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).delete(testItem);
    }

    @Test
    void isNameExist_WhenNameExists_ShouldThrowException() {
        // Arrange
        String name = "Test Item";
        when(itemRepository.findByName(name)).thenReturn(Optional.of(testItem));

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> itemService.isNameExist(name)
        );

        assertEquals("Item already exist with name " + name, exception.getMessage());
        verify(itemRepository, times(1)).findByName(name);
    }

    @Test
    void isNameExist_WhenNameDoesNotExist_ShouldNotThrowException() {
        // Arrange
        String name = "New Item";
        when(itemRepository.findByName(name)).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> itemService.isNameExist(name));

        verify(itemRepository, times(1)).findByName(name);
    }

    // --- getAllItems ---
    @Test
    void getAllItems_ShouldReturnPagedItems() {
        Pageable pageable = mock(Pageable.class);
        Page<Item> page = new PageImpl<>(List.of(testItem));

        when(itemRepository.findAll(pageable)).thenReturn(page);

        Page<Item> result = itemService.getAllItems(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(itemRepository).findAll(pageable);
    }

    // --- getItemById ---
    @Test
    void getItemById_WhenFound_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        Item result = itemService.getItemById(1L);

        assertThat(result.getName()).isEqualTo("Pen");
        verify(itemRepository).findById(1L);
    }

    @Test
    void getItemById_WhenNotFound_ShouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                itemService.getItemById(1L));
    }

    @Test
    void isNameExist_WhenNameNotExists_ShouldDoNothing() {
        when(itemRepository.findByName("NewItem")).thenReturn(Optional.empty());

        itemService.isNameExist("NewItem");

        verify(itemRepository).findByName("NewItem");
    }

    // --- getItemByIdForUpdate ---
    @Test
    void getItemByIdForUpdate_WhenFound_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        Item result = itemService.getItemByIdForUpdate(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getItemByIdForUpdate_WhenNotFound_ShouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                itemService.getItemByIdForUpdate(1L));
    }

    // --- createItem ---
    @Test
    void createItem_WhenNameNotExist_ShouldSaveItem() {
        when(itemRepository.findByName("Pen")).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        Item result = itemService.createItem(testItem);

        assertThat(result.getId()).isEqualTo(1L);
        verify(itemRepository).save(testItem);
    }

    @Test
    void createItem_WhenNameExists_ShouldThrowException() {
        when(itemRepository.findByName("Pen")).thenReturn(Optional.of(testItem));

        assertThrows(BadRequestException.class, () ->
                itemService.createItem(testItem));
    }

    // --- updateItem ---
    @Test
    void updateItem_WhenNameChangedAndNotExist_ShouldUpdate() {
        Item itemDetails = new Item();
        itemDetails.setName("New Laptop");
        itemDetails.setPrice(2000d);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findByName("New Laptop")).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        Item result = itemService.updateItem(1L, itemDetails);

        assertThat(result.getPrice()).isEqualTo(2000d); // your service returns saved item
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_WhenNameChangedAndAlreadyExists_ShouldThrowException() {
        Item itemDetails = new Item();
        itemDetails.setName("Existing");
        itemDetails.setPrice(999D);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findByName("Existing")).thenReturn(Optional.of(new Item()));

        assertThrows(BadRequestException.class, () ->
                itemService.updateItem(1L, itemDetails));
    }

    // --- deleteItem ---
    @Test
    void deleteItem_WhenFound_ShouldDelete() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        itemService.deleteItem(1L);

        verify(itemRepository).delete(testItem);
    }

    @Test
    void deleteItem_WhenNotFound_ShouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                itemService.deleteItem(1L));
    }
}