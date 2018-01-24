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

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.TipoIngrediente;
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
	public String cadastrarUsuario(@Valid Usuario usuario, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
			
		usuario.setPapel(Papel.FUNCIONARIO);
		Usuario salvo = usuarioService.salvar(usuario);
		
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_USUARIO);
		}
		
		if(usuario.getSede() != null) {
			Sede sede = sedeService.buscar(usuario.getSede().getId());
			usuario.setSede(sede);
			usuario.setCidade(sede.getCidade());
			
			Sede novaSede = this.adicionarUsuarioSede(usuario, sede);
			sedeService.salvar(novaSede);
		}
		
		usuarioService.atualizar(salvo);
		
		return "redirect:/";
	}
	
	@GetMapping(path = "/meu_perfil")
	public ModelAndView visualizarUsuario() {
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/detalhesUsuario");
		model.addObject("usuario", usuario);
		return model;
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
	
	@PostMapping(path="/editar")
	public String editarUsuario(@Valid Usuario usuario, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		if(imagem != null && !imagem.isEmpty()) {
			usuario.setFoto64(Image.imagemBase64(imagem));
		}

		Usuario antigo = usuarioService.buscar(usuario.getId());

		if(!antigo.getSede().equals(usuario.getSede())) {
			Sede sede = this.removerUsuarioSede(antigo, antigo.getSede());
			sedeService.salvar(sede);
			
			sede = this.adicionarUsuarioSede(usuario, usuario.getSede());
			sedeService.salvar(sede);
			usuario.setCidade(sede.getCidade());
		}
		usuarioService.salvar(usuario);
		
		return "redirect:/usuario/meu_perfil";
	}
	
	@GetMapping(path="/alterar_senha")
	public ModelAndView alterarSenha() {
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/formAlterarSenha");
		model.addObject("usuario", usuario);
		return model;
	}
	
	@PostMapping(path = "/alterar_senha")
	public String alterarSenha(Usuario usuario, @RequestParam String senhaAtual) throws IOException {
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		Usuario usuarioBanco = usuarioService.buscar(usuarioLogado.getEmail());
		
		if(senhaAtual != null && !senhaAtual.isEmpty()){
			if(usuarioService.compararSenha(senhaAtual, usuarioBanco.getSenha())){
				usuarioBanco.setSenha(usuario.getSenha());
				usuarioService.salvar(usuarioBanco);
				
				Authentication authentication = new UsernamePasswordAuthenticationToken(usuarioBanco, usuarioBanco.getSenha(), usuarioBanco.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				return "redirect:/usuario/meu_perfil";
			}
		}
		return "redirect:/usuario/alterar_senha";
	}
	
	public Sede removerUsuarioSede(Usuario usuario, Sede sede) {
		List<Usuario> clientes = sede.getClientes();
		clientes.remove(usuario);
		
		sede.setClientes(clientes);
		return sede;
	}
	
	public Sede adicionarUsuarioSede(Usuario usuario, Sede sede) {
		List<Usuario> clientes = sede.getClientes();
		clientes.add(usuario);
		
		sede.setClientes(clientes);
		return sede;
	}
	
}
