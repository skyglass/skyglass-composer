package com.cakefactory.catalog;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Item {

    final private String sku;
    final private String title;
    final private BigDecimal price;

}