package com.cakefactory.account;

import com.cakefactory.address.Address;
import com.cakefactory.address.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/account")
class AccountController {

    private final AddressService addressService;

    AccountController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    ModelAndView account(Principal principal) {
        Map<String, String> model = new HashMap<>();

        Address account = this.addressService.findOrEmpty(principal.getName());
        model.put("addressLine1", account.getAddressLine1());
        model.put("addressLine2", account.getAddressLine2());
        model.put("postcode", account.getPostcode());

        return new ModelAndView("account", model);
    }

    @PostMapping
    String updateAccount(Principal principal, String addressLine1, String addressLine2, String postcode) {
        if (principal == null) {
            return "redirect:/login";
        }

        this.addressService.update(principal.getName(), addressLine1, addressLine2, postcode);
        return "redirect:/account";
    }
}
