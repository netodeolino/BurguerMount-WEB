package com.hamburgueria.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/usuario")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastroUsuario(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("usuario/formCadastroUsuario");
		model.addObject(new Usuario());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String salvarUsuario(@Valid Usuario usuario, BindingResult result) {
		if (result.hasErrors()) return "formCadastroUsuario";
		
		Usuario userBanco = usuarioService.salvar(usuario);
		
		return "redirect:/";
	}
}
