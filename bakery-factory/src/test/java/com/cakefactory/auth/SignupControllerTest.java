package com.cakefactory.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SignupControllerTest {

    private SignupService signupService;
    private SignupController signupController;

    @BeforeEach
    void setUp() {
        signupService = mock(SignupService.class);
        signupController = new SignupController(signupService);
    }

    @Test
    void registersUser() {
        signupController.signup("user", "password", "line1", "line2", "P1 CD");
        verify(signupService).register("user", "password", "line1", "line2", "P1 CD");
    }

    @Test
    void redirectsToHomepage() {
        String signupResponse = signupController.signup("user", "password", "line1", "line2", "P1 CD");
        assertThat(signupResponse).isEqualTo("redirect:/");
    }

    @Test
    void redirectsToLoginIfEmailIsTaken() {
        String email = "user@example.com";
        when(signupService.accountExists(email)).thenReturn(true);

        String signupResponse = signupController.signup(email, "password", "line1", "line2", "P1 CD");
        assertThat(signupResponse).isEqualTo("redirect:/login");
    }
}