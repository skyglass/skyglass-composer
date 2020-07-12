package com.cakefactory.basket;

import com.cakefactory.AcceptanceTest;
import com.cakefactory.client.BrowserClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BasketAcceptanceTest extends AcceptanceTest {

    @Test
    void addsItemsToBasket() {
        client.goToHomepage();
        client.clickAddToBasket("Red Velvet");
        client.clickAddToBasket("All Butter Croissant");

        assertThat(client.getBasketItems()).isEqualTo(2);
    }

    @Test
    void addsItemsToBasketForDifferentUsers() {
        client.goToHomepage();
        client.clickAddToBasket("Red Velvet");
        client.clickAddToBasket("Red Velvet");

        BrowserClient anotherClient = new BrowserClient(mockMvc);
        anotherClient.goToHomepage();
        anotherClient.clickAddToBasket("All Butter Croissant");

        assertThat(client.getBasketItems()).isEqualTo(2);
        assertThat(anotherClient.getBasketItems()).isEqualTo(1);
    }

    @Test
    void removesItemsFromBasket() {
        client.goToHomepage();
        client.clickAddToBasket("Red Velvet");
        client.clickAddToBasket("Red Velvet");
        client.clickAddToBasket("Baguette");
        client.goToBasket();
        client.clickRemoveFromBasket("Red Velvet");
        client.clickRemoveFromBasket("Baguette");

        assertThat(client.getBasketItemQtyLabel("Red Velvet")).isEqualTo("1");
        assertThat(client.getBasketItemQtyLabel("Baguette")).isEqualTo("");
    }

    @Test
    void completesOrder() {
        client.goToHomepage();
        client.clickAddToBasket("Baguette");
        client.goToBasket();
        client.fillInAddress("High Rd", "East Finchley", "N2 0NW");
        client.completeOrder();

        assertThat(client.pageText()).contains("Your order is now complete");
    }

    @Test
    void prePopulatesAddressForARegisteredUser() {
        String addressLine1 = "line 1";
        String addressLine2 = "line 2";
        String postcode = "postcode";

        client.goToSignupPage();
        client.fillInDetails("test@example.com", "test", addressLine1, addressLine2, postcode);
        client.completeSignup();

        client.goToBasket();
        assertThat(client.getAddressLine1()).isEqualTo(addressLine1);
        assertThat(client.getAddressLine2()).isEqualTo(addressLine2);
        assertThat(client.getPostcode()).isEqualTo(postcode);
    }
}