package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private static final String USERNAME = "tester";
    private static final String PASSWORD = "testPassword";
    private static final String ITEM = "Round";
    private static final String DESCRIPTION = "A widget that is round";

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitHappyPath() {
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        Item item1 = new Item(1L, ITEM, new BigDecimal("2.99"), DESCRIPTION);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertEquals(cart.getItems(), userOrder.getItems());
        assertEquals(USERNAME, userOrder.getUser().getUsername());
        assertEquals(cart.getTotal(), userOrder.getTotal());
    }

    @Test
    public void testSubmitFail() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrderHappyPath() {
        Cart cart = new Cart();
        User user = new User(1L, USERNAME, PASSWORD, cart);
        Item item1 = new Item(1L, ITEM, new BigDecimal("2.99"), DESCRIPTION);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        cart.setItems(items);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        UserOrder userOrder = UserOrder.createFromCart(cart);
        List<UserOrder> userOrders = Arrays.asList(userOrder);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userOrders, response.getBody());
    }

    @Test
    public void testGetOrderNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCodeValue());
    }
}