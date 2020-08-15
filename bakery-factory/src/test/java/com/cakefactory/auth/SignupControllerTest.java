package com.cakefactory.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.ModelAndView;

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

	@Test
	void redirectsToHomepageIfAlreadySignedUp() {
		String email = "test@example.com";
		when(signupService.accountExists(email)).thenReturn(true);

		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, "", Collections.emptyList());
		ModelAndView signupResponse = signupController.signup(principal);
		assertThat(signupResponse.getViewName()).isEqualTo("redirect:/");
	}

	@Test
	void displaySignupPageIfNotYetSignedUp() {
		String email = "test@example.com";
		when(signupService.accountExists(email)).thenReturn(false);

		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, "", Collections.emptyList());
		ModelAndView signupResponse = signupController.signup(principal);
		assertThat(signupResponse.getViewName()).isEqualTo("signup");
	}

	@Test
	void setsDefaultEmailForAuthenticatedUser() {
		String email = "test@example.com";
		when(signupService.accountExists(email)).thenReturn(false);

		UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(email, "", Collections.emptyList());
		ModelAndView signupResponse = signupController.signup(principal);
		assertThat(signupResponse.getModel().get("e-mail")).isEqualTo(email);
	}

	@Test
	void displaysSignupPageForNonAuthenticatedUser() {
		ModelAndView signupResponse = signupController.signup(null);
		assertThat(signupResponse.getViewName()).isEqualTo("signup");
	}
}
