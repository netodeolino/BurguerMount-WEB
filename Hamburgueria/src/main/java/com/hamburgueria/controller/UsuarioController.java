package com.hamburgueria.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/usuario")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	SedeService sedeService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastroUsuario(HttpServletRequest request) {
		List<Sede> sedes = sedeService.listar();
		
		ModelAndView model = new ModelAndView("usuario/formCadastrarUsuario");
		model.addObject(new Usuario());
		model.addObject("sedes", sedes);
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String salvarUsuario(@Valid Usuario usuario, BindingResult result, Long idsede) {
		//if (result.hasErrors()) return "usuario/formCadastroUsuario";
		System.err.println("ENTROU");
		if(idsede != null) {
			Sede sede = sedeService.buscar(idsede);
			if (sede != null) {
				usuario.setSede(sede);
				usuario.setCidade(sede.getCidade());
			}
		}
				
		usuario.setPapel(Papel.ADMINISTRADOR);
		
		usuarioService.salvar(usuario);
		
		return "redirect:/";
	}
}
