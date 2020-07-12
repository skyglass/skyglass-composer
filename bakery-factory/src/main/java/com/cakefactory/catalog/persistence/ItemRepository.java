package com.cakefactory.catalog.persistence;

import org.springframework.data.repository.CrudRepository;

interface ItemRepository extends CrudRepository<ItemEntity, String> {
    ItemEntity findBySku(String sku);
}