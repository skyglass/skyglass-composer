package com.cakefactory.catalog;

public interface CatalogService {

    Iterable<Item> getItems();

	Item getItemBySku(String sku);

}