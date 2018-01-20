package com.hamburgueria.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/usuario")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	SedeService sedeService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarUsuario(HttpServletRequest request) {
		List<Sede> sedes = sedeService.listar();
		
		ModelAndView model = new ModelAndView("usuario/formCadastrarUsuario");
		model.addObject(new Usuario());
		model.addObject("sedes", sedes);
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarUsuario(@Valid Usuario usuario, BindingResult result, Long idsede, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		//if (result.hasErrors()) return "usuario/formCadastroUsuario";

		if(idsede != null) {
			Sede sede = sedeService.buscar(idsede);
			if (sede != null) {
				usuario.setSede(sede);
				usuario.setCidade(sede.getCidade());
			}
		}
		usuario.setPapel(Papel.ADMINISTRADOR);
		Usuario salvo = usuarioService.salvar(usuario);
		
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_USUARIO);
		}
		usuarioService.salvar(salvo);
		
		return "redirect:/";
	}
	
	@GetMapping(path = "/editar")
	public ModelAndView editarUsuario() {
		List<Sede> sedes = sedeService.listar();
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/formEditarUsuario");
		model.addObject("usuario", usuario);
		model.addObject("sedes", sedes);
		return model;
	}
	
	@PostMapping(path = "/editar")
	public String editarUsuario(Usuario usuario, @RequestParam String senhaAtual, Long idsede, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		Usuario usuarioBanco = usuarioService.buscar(usuarioLogado.getEmail());
		
		if(imagem != null && !imagem.isEmpty()) {
			usuarioBanco.setFoto64(Image.imagemBase64(imagem));
		}
		
		if(!usuario.getNome().isEmpty()) {
			usuarioBanco.setNome(usuario.getNome());
		}
		
		if(!usuario.getEmail().isEmpty()) {
			usuarioBanco.setEmail(usuario.getEmail());
		}
		
		if(idsede != null) {
			Sede sede = sedeService.buscar(idsede);
			if (sede != null) {
				usuarioBanco.setSede(sede);
				usuarioBanco.setCidade(sede.getCidade());
			}
		}
		
		usuarioBanco.setPedidos(usuario.getPedidos());
		usuarioBanco = usuarioService.salvar(usuarioBanco);
		
		if(senhaAtual != null && !senhaAtual.isEmpty()){
			if(usuarioService.compararSenha(usuarioBanco.getSenha(), senhaAtual)){
				usuarioBanco.setSenha(usuario.getSenha());
				usuarioService.salvar(usuarioBanco);
			}
		}
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(usuarioBanco, usuarioBanco.getSenha(), usuarioBanco.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/usuario/editar";
	}
}
