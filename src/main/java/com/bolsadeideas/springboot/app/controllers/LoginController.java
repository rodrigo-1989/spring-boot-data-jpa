package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping(value = "/login")
	public String login(@RequestParam (value = "error" ,required = false) String error,Model model, Principal principal) {
		
		if(principal != null) {
			System.out.println("*****Ya has iniciado session*****");
			return "redirect:/";
		}
		if(error != null) {
			model.addAttribute("error","Error en en las credenciales");
		}
		return "login";
	}
}
