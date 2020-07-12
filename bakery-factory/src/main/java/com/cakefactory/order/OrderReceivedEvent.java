package com.cakefactory.order;

import com.cakefactory.basket.BasketItem;
import lombok.Data;

import java.util.Collection;

@Data
public class OrderReceivedEvent {

    private final String deliveryAddress;
    private final Collection<BasketItem> items;

}
