package com.hamburgueria.controller;

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

import com.hamburgueria.model.Pedido;
import com.hamburgueria.model.Status;
import com.hamburgueria.service.PedidoService;

@Controller
@RequestMapping(path="/pedido")
public class PedidoController {
	
	@Autowired
	PedidoService pedidoService;

	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarPedido() {		
		ModelAndView model = new ModelAndView("pedido/formCadastroPedido");
		model.addObject(new Pedido());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarPedido(@Valid Pedido pedido) {
		pedido.setStatus(Status.FEITO);
		pedidoService.salvar(pedido);
		return "redirect:/pedido/listar";
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
	
}
