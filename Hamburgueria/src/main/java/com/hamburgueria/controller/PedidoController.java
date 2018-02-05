package com.hamburgueria.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Pedido;
import com.hamburgueria.model.Status;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.PedidoService;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/pedido")
public class PedidoController {
	
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	UsuarioService usuarioService;

	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarPedido() {		
		ModelAndView model = new ModelAndView("pedido/formCadastroPedido");
		model.addObject(new Pedido(0.0));
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public ModelAndView cadastrarPedido(@Valid Pedido pedido) {
		Date today = new Date();
		pedido.setPreco(pedido.getPreco());
		pedido.setData(today);
		pedido.setStatus(Status.EM_ABERTO);
		pedido.setCliente(usuarioService.usuarioLogado());
		Pedido pedidoBanco = pedidoService.salvar(pedido);
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("pedido/formAdicionarIngredientes");
		model.addObject("pedido", pedidoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	public ModelAndView cadastrarPedido(Long id) {
		Pedido pedidoBanco = pedidoService.buscar(id);
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("pedido/formAdicionarIngredientes");
		model.addObject("pedido", pedidoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarPedidos(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listar();
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirPedido(@PathVariable("id") Long id) {
		pedidoService.excluir(id);
		return "redirect:/sede/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarPedido(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscar(id);
		ModelAndView model = new ModelAndView("pedido/formEditarPedido");
		model.addObject("pedido", pedido);
		return model;
	}
	
	/*Função que salva o pedido modificado.
	 *Recebe uma pedido e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarPedido(@Valid Pedido pedido, BindingResult result) { 
		pedidoService.salvar(pedido);
		return "redirect:/sede/listar";
	}
	
	@PostMapping(path="/{id}/selecionar_ingredientes")
 	public ModelAndView adicionarIngredientes(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) {
		Pedido pedido = pedidoService.buscar(id);
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
		
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		List<Ingrediente> ingredientesJaSalvos = pedido.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		pedido.setPreco(pedido.getPreco() + (ingrediente.getValorDeVenda() * quantidade));
 		pedido.setIngredientes(ingredientesJaSalvos);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
		
 		return cadastrarPedido(pedidoAtualizado.getId());
	}
	
	@GetMapping(path="/{id_pedido}/remover_ingrediente/{id_ingrediente}")
 	public ModelAndView removerIngredientes(@PathVariable("id_pedido") Long id_pedido, @PathVariable("id_ingrediente") Long id_ingrediente) {
 		Pedido pedido = pedidoService.buscar(id_pedido);
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
 		
 		List<Ingrediente> ingredientesDoProduto = pedido.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		pedido.setPreco(pedido.getPreco() - (ingrediente.getValorDeVenda()));
 		pedido.setIngredientes(ingredientesDoProduto);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		return cadastrarPedido(pedidoAtualizado.getId());
	}
	
	@GetMapping(path="/finalizar_pedido")
 	public String adicionarIngredientes() {
 		return "redirect:/produto/listar";
	}
}
