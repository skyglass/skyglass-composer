package com.cakefactory.basket;

import com.cakefactory.catalog.CatalogService;
import com.cakefactory.catalog.Item;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
class SessionBasket implements Basket {

    private final Map<String, BasketItem> items = new ConcurrentHashMap<>();
    private final CatalogService catalogService;

    public SessionBasket(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void add(String sku) {
        Item item = this.catalogService.getItemBySku(sku);
        this.items.compute(sku, (existingSku, existingItem) -> {
            if (existingItem == null) {
                return new BasketItem(item, 1);
            }

            return new BasketItem(existingItem.getItem(), existingItem.getQty() + 1);
        });
    }

    @Override
    public int getTotalItems() {
        return this.items.values().stream().map(BasketItem::getQty).reduce(0, Integer::sum);
    }

    @Override
    public Collection<BasketItem> getItems() {
        return this.items.values();
    }

    @Override
    public void remove(String sku) {
        this.items.computeIfPresent(sku, (s, existingItem) -> {
            if (existingItem.getQty() == 1) {
                return null;
            }

            return new BasketItem(existingItem.getItem(), existingItem.getQty() - 1);
        });
    }

    @Override
    public void clear() {
        this.items.clear();
    }

}