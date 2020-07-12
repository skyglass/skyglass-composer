package com.cakefactory.order;

import com.cakefactory.basket.Basket;
import com.cakefactory.basket.BasketItem;
import com.cakefactory.catalog.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.*;

// this a simple unit test, so no @SpringBootTest or @WebMvc annotations
class OrderControllerTest {

    private OrderController controller;
    private Basket basket;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        this.basket = mock(Basket.class);
        this.eventPublisher = mock(ApplicationEventPublisher.class);
        this.controller = new OrderController(this.basket, this.eventPublisher);
    }

    @Test
    void clearsBasket() {
        this.controller.completeOrder("", "", "");

        verify(this.basket).clear();
    }

    @Test
    void publishesOrder() {
        BasketItem basketItem1 = new BasketItem(new Item("sku1", "Test", BigDecimal.TEN), 3);
        BasketItem basketItem2 = new BasketItem(new Item("sku2", "Test", BigDecimal.TEN), 3);
        when(this.basket.getItems()).thenReturn(Arrays.asList(basketItem1, basketItem2));

        this.controller.completeOrder("line1", "line2", "P1 CD");

        verify(this.eventPublisher).publishEvent(new OrderReceivedEvent("line1, line2, P1 CD", Arrays.asList(basketItem1, basketItem2)));
    }
}