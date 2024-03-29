package com.hamburgueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/")
public class MainController {
	
	@Autowired
	UsuarioService usuarioService;
	
	@GetMapping(path="")
	public ModelAndView loginUsuario() {
		ModelAndView model = new ModelAndView("index");
		model.addObject("usuario", new Usuario());
		return model;
	}
	
	@RequestMapping(path="erroLogin")
	public String loginErrorUsuario(RedirectAttributes attributes) {
		attributes.addFlashAttribute("mensagem", "Email ou senha incorretos!");
		return "redirect:/";
	}
	
}
