package com.hamburgueria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Usuario;

@Controller
@RequestMapping(path="/")
public class MainController {

	@GetMapping(path="")
	public ModelAndView loginUsuario() {
		ModelAndView model = new ModelAndView("index");
		model.addObject("usuario", new Usuario());
		return model;
}
}
