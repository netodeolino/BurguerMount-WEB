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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Pedido;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.PedidoService;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.TipoIngredienteService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/sede")
public class SedeController {

	@Autowired
	SedeService sedeService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	TipoIngredienteService tipoIngredienteService;
	
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	UsuarioController usuarioController;
	
	@Autowired
	TipoIngredienteController tipoIngredienteController;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroSede" uma sede vazia e uma lista de  sedes
	 */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarSede(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("sede/formCadastroSede");
		model.addObject(new Sede());
		return model;
	}
	
	/*Função que salva a sede cadastrada.
	 *Recebe uma sede e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public String cadastrarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem, RedirectAttributes attributes) throws IOException {
		Sede salva = sedeService.salvar(sede);
		
		//Verifica se foi informada uma imagem, caso não: o ingrediente é salvo com uma imagem padrão.
		if (imagem != null && !imagem.isEmpty()) {
			salva.setFoto64(Image.imagemBase64(imagem));
		} else {
			salva.setFoto64(Constants.IMAGE_DEFAULT_SEDE);
		}
		
		sedeService.salvar(salva);
		
		attributes.addFlashAttribute("mensagemCadastro", "Cadastro realizado com Sucesso!");
		return "redirect:/sede/listar";
	}
	
	/*Função que lista todas sedes do banco.
	 *Retorna pra página "listarSedes" essas sedes.
	 */
	@GetMapping(path="/listar")
	public ModelAndView listarSedes(){
		ModelAndView model = new ModelAndView("sede/listarSedes");
		List<Sede> sedes = sedeService.listar();
		model.addObject("sedes", sedes);		
		return model;
	}
	
	//Função que exclui uma determinada sede.
	@GetMapping(path="/excluir/{id}")
	public String excluirSede(@PathVariable("id") Long id) {
		Sede sede = sedeService.buscar(id);
		this.excluirTipoIngredientesSede(sede);
		this.removerPedidosSede(sede);
		this.removerSedeUsuarios(sede);
		
		sedeService.excluir(id);
		
		return "redirect:/sede/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarSede" uma sede informada pela URL.
	 */
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarSede(@PathVariable("id") Long id) {
		Sede sede = sedeService.buscar(id);
		ModelAndView model = new ModelAndView("sede/formEditarSede");
		model.addObject("sede", sede);
		return model;
	}
	
	/*Função que salva a sede modificada.
	 *Recebe uma sede e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if(imagem != null && !imagem.isEmpty()) {
			sede.setFoto64(Image.imagemBase64(imagem));
		}
		sedeService.salvar(sede);
		
		return "redirect:/sede/listar";
	}
	
	//Função que permite um usuário MASTER alterar a sua sede.
	@GetMapping(path="/alterar_sede/{id_sede}")
	public String alterarSede(@PathVariable("id_sede") Long id_sede) {
		Sede sede = sedeService.buscar(id_sede);
		//Verifica se a sede informada existe, caso contrário é redirecionado para página de listar as sedes.
		if(sede == null)
			return "redirect:/sede/listar";
		
		//Altera a sede do usuário logado.
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		usuarioLogado.setSede(sede);
		
		Usuario usuarioAntigo = usuarioService.buscar(usuarioLogado.getId());
		
		//Verifica se a sede foi alterada.
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
	
	//Função que remove a sede do usuário.
	public void removerSedeUsuarios(Sede sede) {
		//Para todos os usuário da sede, altera o papel dos mesmos para CLIENTE e remove a sua sede.
		System.out.println("AQUIIIIIIIIIIIIIIIIIII");
		for (Usuario cliente : usuarioService.listarTodos()) {
			if (cliente.getSede().getId() == sede.getId()) {
				cliente.setSede(null);
				cliente.setPapel(Papel.CLIENTE);
				usuarioService.atualizar(cliente);
			}
		}
	}
	
	//Exclui todos os produtos de uma sede.
	public void excluirTipoIngredientesSede(Sede sede) {
		List<TipoIngrediente> tipoIngredientes = sede.getTipoIngredientes();
		Integer tamanho = tipoIngredientes.size();
		
		for (int i = 0; i < tamanho; i++) {
			this.excluirTipoIngrediente(tipoIngredientes.get(0));
		}
	}
	
	//Exclui um determinado produto.
	public void excluirTipoIngrediente(TipoIngrediente tipoIngrediente) {
		tipoIngredienteController.removerTipoIngredienteSede(tipoIngrediente, tipoIngrediente.getSede());
		tipoIngredienteController.excluirIngredientesTipoIngrediente(tipoIngrediente);
		tipoIngredienteService.excluir(tipoIngrediente.getId());
	}
	
	//Função que remove a sede do usuário.
	public void removerPedidosSede(Sede sede) {
		List<Pedido> pedidos = pedidoService.listarTodos(sede.getId());
		for (Pedido pedido : pedidos) {
			pedido.setSede(null);
			pedidoService.salvar(pedido);
		}
	}

}
