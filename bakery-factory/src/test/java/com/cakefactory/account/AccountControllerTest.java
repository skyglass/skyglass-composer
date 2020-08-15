package com.cakefactory.account;

import com.cakefactory.address.Address;
import com.cakefactory.address.AddressService;
import com.cakefactory.basket.Basket;
import com.cakefactory.client.BrowserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(OAuth2ClientAutoConfiguration.class)
class AccountControllerTest {

    private final String TEST_EMAIL = "test@example.com";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddressService addressService;

    @MockBean
    AccountService accountService;

    @MockBean
    Basket basket;

    private BrowserClient browserClient;

    @BeforeEach
    void setUp() {
        this.browserClient = new BrowserClient(mockMvc);
    }

    @Test
    void onlyAllowsAccessForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", endsWith("/login")));
    }

    @Test
    @WithMockUser(TEST_EMAIL)
    void populatesAddressForExistingAccount() {
        when(addressService.findOrEmpty(TEST_EMAIL)).thenReturn(new Address("line 1", "line 2", "postcode"));

        browserClient.goToAccountPage();
        assertThat(browserClient.getAddressLine1()).isEqualTo("line 1");
        assertThat(browserClient.getAddressLine2()).isEqualTo("line 2");
        assertThat(browserClient.getPostcode()).isEqualTo("postcode");
    }

    @Test
    @WithMockUser(TEST_EMAIL)
    void updatesAddress() {
        when(addressService.findOrEmpty(TEST_EMAIL)).thenReturn(new Address("line 1", "line 2", "postcode"));

        browserClient.goToAccountPage();
        browserClient.fillInAddress("new line 1", "new line 2", "postcode");
        browserClient.clickPrimaryButton();

        verify(addressService).update(TEST_EMAIL, "new line 1", "new line 2", "postcode");
    }
}