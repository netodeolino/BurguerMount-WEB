package com.hamburgueria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.Papel;
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
		System.out.println("Email ou senha incorretos!");
		return "redirect:/";
	}
	
	@GetMapping(path="gerenciar")
	public ModelAndView gerenciar() {
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		
		if (usuarioLogado.getPapel() == Papel.ADMINISTRADOR) {
			ModelAndView model = new ModelAndView("admin");
			model.addObject("usuario", usuarioLogado);
			return model;
		}
		
		if (usuarioLogado.getPapel() == Papel.ATENDENTE) {
			ModelAndView model = new ModelAndView("atendente");
			model.addObject("usuario", usuarioLogado);
			return model;
		}
		
		ModelAndView model = new ModelAndView("index");
		model.addObject("usuario", new Usuario());
		return model;
	}
}
