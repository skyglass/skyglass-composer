package com.cakefactory.catalog;

import com.cakefactory.basket.Basket;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
class CatalogController {

    private final CatalogService catalogService;
    private final Basket basket;

    CatalogController(CatalogService catalogService, Basket basket) {
        this.catalogService = catalogService;
        this.basket = basket;
    }

    @GetMapping("/")
    ModelAndView index() {
        Map<String, Object> model = new HashMap<>();
        model.put("items", this.catalogService.getItems());

        return new ModelAndView("catalog", model);
    }

}