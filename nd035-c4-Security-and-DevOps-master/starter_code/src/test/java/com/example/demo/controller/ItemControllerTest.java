package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private static final String ITEM = "Round";
    private static final String DESCRIPTION = "A widget that is round";

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetAllItems() {
        Item item1 =  new Item(1L, ITEM, new BigDecimal(2.99), DESCRIPTION);
        Item item2 = new Item(2L, "Square", new BigDecimal(1.99), "A widget that is square");
        List<Item> items = new ArrayList();
        items.add(item1);
        items.add(item2);
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    @Test
    public void testFindItemHappyPath() {
        Item item1 =  new Item(1L, ITEM, new BigDecimal(2.99), DESCRIPTION);
        // Find by id
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ResponseEntity<Item> responseById = itemController.getItemById(1L);
        assertNotNull(responseById);
        assertEquals(200, responseById.getStatusCodeValue());
        assertEquals(item1, responseById.getBody());

        // Find by name
        List<Item> items = Arrays.asList(item1);
        when(itemRepository.findByName(ITEM)).thenReturn(items);
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName(ITEM);
        assertNotNull(responseByName);
        assertEquals(200, responseByName.getStatusCodeValue());
        assertEquals(items, responseByName.getBody());
    }

    @Test
    public void testFindItemNotFound() {
        // Find by id
        ResponseEntity<Item> responseById = itemController.getItemById(1L);
        assertNull(responseById.getBody());
        assertEquals(404, responseById.getStatusCodeValue());

        // Find by name
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName(ITEM);
        assertNull(responseByName.getBody());
        assertEquals(404, responseByName.getStatusCodeValue());
    }
}