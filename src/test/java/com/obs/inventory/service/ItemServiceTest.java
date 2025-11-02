package com.obs.inventory.service;

import com.obs.inventory.entity.Item;
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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Pen");
        testItem.setPrice(5.0);
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
}