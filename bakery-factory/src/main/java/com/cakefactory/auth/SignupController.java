package com.cakefactory.auth;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/signup")
class SignupController {

	private final SignupService signupService;

	public SignupController(SignupService signupService) {
		this.signupService = signupService;
	}

	@GetMapping
	ModelAndView signup(Principal principal) {
		if (principal != null && signupService.accountExists(principal.getName())) {
			return new ModelAndView("redirect:/");
		}

		return new ModelAndView("signup", Map.of("e-mail", principal == null ? "" : principal.getName()));
	}

	@PostMapping
	String signup(String email, String password, String addressLine1, String addressLine2, String postcode) {
		if (this.signupService.accountExists(email)) {
			return "redirect:/login";
		}

		this.signupService.register(email, password, addressLine1, addressLine2, postcode);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);
		return "redirect:/";
	}
}
