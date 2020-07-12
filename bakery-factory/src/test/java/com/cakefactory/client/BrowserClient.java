package com.cakefactory.client;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;

import java.util.List;

@Slf4j
public class BrowserClient {

    private final WebClient webClient;
    private HtmlPage currentPage;

    public BrowserClient(MockMvc mockMvc) {
        this.webClient = MockMvcWebClientBuilder.mockMvcSetup(mockMvc).build();
    }

    @SneakyThrows
	public void goToHomepage() {
        this.currentPage = this.webClient.getPage("http://localhost");
	}

    @SneakyThrows
	public void clickAddToBasket(String title) {
        List<DomNode> itemCards = this.currentPage.getByXPath(String.format("//div[descendant::a[text()='%s'] and @class='card h-100']", title));
        if (itemCards.size() != 1) {
            log.warn("No item card found for {}", title);
            return;
        }

        HtmlElement addButton = itemCards.get(0).querySelector(".add-to-basket");
        if (addButton == null) {
            log.warn("No add button found for {}", title);
            return;
        }

        this.currentPage = addButton.click();
	}

	public Integer getBasketItems() {
        try {
            String basketTotalValue = this.currentPage.querySelector(".basket-total").asText();
            return Integer.parseInt(basketTotalValue);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    @SneakyThrows
    public void goToBasket() {
        this.currentPage = this.webClient.getPage("http://localhost/basket");
    }

	public String getBasketItemQtyLabel(String title) {
        DomNode itemRow = getBasketItemRow(title);
        if (itemRow == null) {
            return "";
        }

		return itemRow.querySelector(".qty").asText();
	}

	@SneakyThrows
    public void clickRemoveFromBasket(String title) {
        DomNode itemRow = getBasketItemRow(title);
        if (itemRow == null) {
            return;
        }

        HtmlElement deleteButton = itemRow.querySelector(".btn.remove-item");
        this.currentPage = deleteButton.click();
    }

    public void fillInAddress(String line1, String line2, String postcode) {
        setValue("#addressLine1", line1);
        setValue("#addressLine2", line2);
        setValue("#postcode", postcode);
    }

    @SneakyThrows
    public void completeOrder() {
        HtmlElement completeOrderButton = this.currentPage.querySelector("#complete-order");
        this.currentPage = completeOrderButton.click();
    }

    public String pageText() {
        return this.currentPage.asText();
    }

    private DomNode getBasketItemRow(String title) {
        List<DomNode> items = this.currentPage.getByXPath(String.format("//tr[descendant::td[text()='%s']]", title));
        if (items.size() != 1) {
            log.warn("No item found with title {}", title);
            return null;
        }

        return items.get(0);
    }

    @SneakyThrows
    public void goToSignupPage() {
        this.currentPage = this.webClient.getPage("http://localhost/signup");
    }

    @SneakyThrows
    public void goToAccountPage() {
        this.currentPage = this.webClient.getPage("http://localhost/account");
    }

    public void fillInDetails(String email, String password, String addressLine1, String addressLine2, String postcode) {
        setValue("#email", email);
        setValue("#password", password);
        fillInAddress(addressLine1, addressLine2, postcode);
    }

    @SneakyThrows
    public void completeSignup() {
        HtmlButton signupButton = this.currentPage.querySelector("#signup");
        this.currentPage = signupButton.click();
    }

    public String getCurrentUserEmail() {
        return this.currentPage.querySelector("#current-user").asText();
    }

    @SneakyThrows
    public void goToLoginPage() {
        this.currentPage = this.webClient.getPage("http://localhost/login");
    }

    public void fillInLogin(String email, String password) {
        setValue("#username", email);
        setValue("#password", password);
    }

    @SneakyThrows
    public void clickPrimaryButton() {
        HtmlButton loginButton = this.currentPage.querySelector(".btn-primary");
        this.currentPage = loginButton.click();
    }

    public String getAddressLine1() {
        return this.currentPage.querySelector("#addressLine1").asText();
    }

    public String getAddressLine2() {
        return this.currentPage.querySelector("#addressLine2").asText();
    }

    public String getPostcode() {
        return this.currentPage.querySelector("#postcode").asText();
    }

    private void setValue(String selector, String value) {
        HtmlInput input = this.currentPage.querySelector(selector);
        input.setValueAttribute(value);

    }
}