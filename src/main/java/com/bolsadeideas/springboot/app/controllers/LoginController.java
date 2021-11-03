package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping(value = "/login")
	public String login(Model model, Principal principal) {
		
		if(principal != null) {
			System.out.println("*****Ya has iniciado session*****");
			return "redirect:/";
		}
		return "login";
	}
}
