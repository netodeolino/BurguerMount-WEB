package com.hamburgueria.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Sede;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/master")
public class MasterController {
	
	@Autowired
	SedeService sedeService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	UsuarioController usuarioController;
	
	@GetMapping(path="/escolher_sede")
	public ModelAndView escolherSede() {
		ModelAndView model = new ModelAndView("sede/listarSedes");
		List<Sede> sedes = sedeService.listar();
		model.addObject("sedes", sedes);		
		return model;
	}
	
	@GetMapping(path="/alterar_sede/{id_sede}")
	public String alterarSede(@PathVariable("id_sede") Long id_sede) {
		Sede sede = sedeService.buscar(id_sede);
		if(sede == null)
			return "redirect:/master/escolher_sede";
		
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		usuarioLogado.setSede(sede);
		
		Usuario usuarioAntigo = usuarioService.buscar(usuarioLogado.getId());
		
		if(usuarioAntigo.getSede()!= null && !usuarioAntigo.getSede().equals(usuarioLogado.getSede())) {
			usuarioController.removerUsuarioSede(usuarioAntigo, usuarioAntigo.getSede());
			
			usuarioController.adicionarUsuarioSede(usuarioLogado, usuarioLogado.getSede());
		}
		
		usuarioService.atualizar(usuarioLogado);
		
		//Atualiza as informações do usuário logado.
		Authentication authentication = new UsernamePasswordAuthenticationToken(usuarioLogado, usuarioLogado.getSenha(), usuarioLogado.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/tipo_ingrediente/listar";
	}
	
	@GetMapping(path="/listar_usuarios")
	public ModelAndView listarUsuarios() {
		List<Usuario> usuarios = usuarioService.listarTodos();
		usuarios.remove(usuarioService.usuarioLogado());
		
		ModelAndView model = new ModelAndView("usuario/listarUsuarios");
		model.addObject("usuarios", usuarios);
		return model;
	}
	
	@GetMapping(path="/perfil_usuario/{id_usuario}")
	public ModelAndView visualizarUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		ModelAndView model = new ModelAndView("usuario/detalhesUsuario");
		model.addObject("usuario", usuario);
		return model;
	}
	
	@GetMapping(path="/usuario/editar_papel/{id_usuario}")
	public ModelAndView editarPapelUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		ModelAndView model = new ModelAndView("usuario/alterarPapel");
		model.addObject("usuario", usuario);
		return model;
	}
	
	@PostMapping(path="/usuario/editar_papel")
	public String editarPapelUsuario(@Valid Usuario usuario) {
		System.err.println("OI");
		System.err.println(usuario.getPapel());
		Usuario usuarioBanco = usuarioService.buscar(usuario.getId());
		
		if(usuario.getPapel().equals(usuarioBanco.getPapel()))
			return "redirect:/master/listar_usuarios";
		
		usuarioService.atualizar(usuario);
		return "redirect:/master/perfil_usuario/" + usuario.getId();
	}
	
}
