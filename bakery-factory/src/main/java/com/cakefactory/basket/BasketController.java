package com.cakefactory.basket;

import com.cakefactory.address.Address;
import com.cakefactory.address.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;

@Controller
@RequestMapping("/basket")
class BasketController {

    private final Basket basket;
    private final AddressService addressService;

    BasketController(Basket basket, AddressService addressService) {
        this.basket = basket;
        this.addressService = addressService;
    }

    @PostMapping
    String addToBasket(@RequestParam String sku) {
        this.basket.add(sku);
        return "redirect:/";
    }

    @GetMapping
    ModelAndView showBasket(Principal principal) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("items", basket.getItems());
        if (principal != null) {
            Address address = this.addressService.findOrEmpty(principal.getName());
            model.put("addressLine1", address.getAddressLine1());
            model.put("addressLine2", address.getAddressLine2());
            model.put("postcode", address.getPostcode());
        } else {
            model.put("addressLine1", "");
            model.put("addressLine2", "");
            model.put("postcode", "");
        }

        return new ModelAndView("basket", model);
    }

    @PostMapping("/delete")
    String removeFromBasket(@RequestParam String sku) {
        this.basket.remove(sku);
        return "redirect:/basket";
    }

}