package com.cakefactory.auth;

import com.cakefactory.account.AccountService;
import com.cakefactory.address.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultSignupServiceTest {

    private DefaultSignupService signupService;
    private AccountService accountServiceMock;
    private AddressService addressServiceMock;

    @BeforeEach
    void setUp() {
        this.accountServiceMock = mock(AccountService.class);
        this.addressServiceMock = mock(AddressService.class);
        this.signupService = new DefaultSignupService(accountServiceMock, addressServiceMock);
    }

    @Test
    void createsUserAccount() {
        String email = "test@example.com";
        String password = "password";
        this.signupService.register(email, password, "line 1", "line 2", "postcode");
        verify(accountServiceMock).register(email, password);
    }

    @Test
    void updatesUserAddress() {
        String addressLine1 = "line 1";
        String addressLine2 = "line 2";
        String postcode = "postcode";
        this.signupService.register("test@example.com", "password", addressLine1, addressLine2, postcode);
        verify(addressServiceMock).update("test@example.com", addressLine1, addressLine2, postcode);
    }
}