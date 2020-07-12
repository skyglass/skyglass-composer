package com.cakefactory.catalog.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import com.cakefactory.catalog.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class JpaCatalogServiceTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    ItemRepository itemRepository;

    private JpaCatalogService jpaCatalogService;

    @BeforeEach
    void setup() {
        this.jpaCatalogService = new JpaCatalogService(this.itemRepository);
    }

    @Test
    @DisplayName("returns data from the database")
    void returnsDataFromDatabase() {
        String expectedTitle = "Victoria Sponge";
        String expectedSku = saveTestItem(expectedTitle, BigDecimal.valueOf(5.55));

        Iterable<Item> items = jpaCatalogService.getItems();

        assertThat(items).anyMatch(item -> expectedTitle.equals(item.getTitle()) && expectedSku.equals(item.getSku()));
    }

    @Test
    @DisplayName("returns a single item from the database")
    void returnsItemBySku() {
        String expectedTitle = "Victoria Sponge";
        String expectedSku = saveTestItem(expectedTitle, BigDecimal.valueOf(5.55));

        Item itemBySku = jpaCatalogService.getItemBySku(expectedSku);

        assertThat(itemBySku.getTitle()).isEqualTo(expectedTitle);
    }

    private String saveTestItem(String title, BigDecimal price) {
        ItemEntity itemEntity = new ItemEntity();
        String sku = UUID.randomUUID().toString().replace("-", "");
        itemEntity.sku = sku;
        itemEntity.title = title;
        itemEntity.price = price;

        testEntityManager.persistAndFlush(itemEntity);

        return sku;
    }

}