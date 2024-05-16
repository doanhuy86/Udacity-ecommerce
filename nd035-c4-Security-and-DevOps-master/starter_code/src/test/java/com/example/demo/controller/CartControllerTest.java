package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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

public class CartControllerTest {
    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testPassword";
    private static final String ITEM = "Round";
    private static final String DESCRIPTION = "A widget that is round";

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart() {
        Item item1 = new Item(1L, ITEM, new BigDecimal("2.99"), DESCRIPTION);
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        ResponseEntity<Cart> responseAdd = cartController.addTocart(modifyCartRequest);
        assertNotNull(responseAdd);
        assertEquals(200, responseAdd.getStatusCodeValue());
        Cart returnedCart = responseAdd.getBody();
        assertEquals(2, returnedCart.getItems().size());
        assertEquals(new BigDecimal("5.98"), returnedCart.getTotal());
        assertEquals(user, returnedCart.getUser());

    }

    @Test
    public void testRemoveFromCart() {
        Item item1 = new Item(1L, ITEM, new BigDecimal("2.99"), DESCRIPTION);
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        modifyCartRequest = createCartRequest();
        ResponseEntity<Cart> responseRemove = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(responseRemove);
        assertEquals(200, responseRemove.getStatusCodeValue());
        Cart returnedCart = responseRemove.getBody();
        assertNotNull(returnedCart);
        assertEquals(0, returnedCart.getItems().size());
        assertEquals(new BigDecimal("0.00"), returnedCart.getTotal());
        assertEquals(user, returnedCart.getUser());
    }

    @Test
    public void testAddFail() {
        // User not found
        ModifyCartRequest modifyCartRequest = createCartRequest();
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserNotFound = cartController.addTocart(modifyCartRequest);
        assertNull(responseUserNotFound.getBody());
        assertEquals(404, responseUserNotFound.getStatusCodeValue());

        // Item not found
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemNotFound = cartController.addTocart(modifyCartRequest);
        assertNull(responseItemNotFound.getBody());
        assertEquals(404, responseItemNotFound.getStatusCodeValue());
    }

    @Test
    public void testRemoveFail() {
        // User not found
        ModifyCartRequest modifyCartRequest = createCartRequest();
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserNotFound = cartController.removeFromcart(modifyCartRequest);
        assertNull(responseUserNotFound.getBody());
        assertEquals(404, responseUserNotFound.getStatusCodeValue());

        // Item not found
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemNotFound = cartController.removeFromcart(modifyCartRequest);
        assertNull(responseItemNotFound.getBody());
        assertEquals(404, responseItemNotFound.getStatusCodeValue());
    }

    private ModifyCartRequest createCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(USERNAME);
        return modifyCartRequest;
    }
}