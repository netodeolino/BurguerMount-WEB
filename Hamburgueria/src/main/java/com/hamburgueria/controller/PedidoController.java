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

import com.hamburgueria.model.Pedido;
import com.hamburgueria.model.Produto;
import com.hamburgueria.model.Status;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.PedidoService;
import com.hamburgueria.service.ProdutoService;
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
	
	@Autowired
	ProdutoService produtoService;
	
	@GetMapping(path="/lanches_prontos")
	public ModelAndView lanchesProntos() {		
		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		Pedido pedido = new Pedido();
		pedido.setStatus(Status.EM_ABERTO);
		pedido.setPreco(0.0);
		pedido.setDisponivel(true);
		pedido.setSede(usuarioService.usuarioLogado().getSede());
		
		Pedido pedidoSalvo = pedidoService.salvar(pedido);
		
		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoSalvo);
		return model;
	}
	
	@GetMapping(path="/lanches_prontos/editar/{id}")
	public ModelAndView editarLanchesProntos(@PathVariable("id") Long id) {		
		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		Pedido pedido = pedidoService.buscar(id);
		
		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedido);
		return model;
	}

	@GetMapping(path="/cadastrar/{id}")
	public ModelAndView cadastrarPedido(@PathVariable("id") Long id) {		
		Pedido pedido = pedidoService.buscar(id);
		ModelAndView model = new ModelAndView("pedido/formCadastroPedido");
		model.addObject("pedido", pedido);
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarPedido(@Valid Pedido pedido) {
		pedido.setCliente(usuarioService.usuarioLogado());
		Date today = new Date();
		pedido.setData(today);
		pedido.setPreco(pedido.getPreco());
		pedido.setStatus(Status.EM_ANDAMENTO);
		pedido.setSede(usuarioService.usuarioLogado().getSede());
		
		pedidoService.salvar(pedido);
		
		return "redirect:/pedido/listar/todos";
	}
	
	@GetMapping(path="/listar/todos")
	public ModelAndView listarPedidos(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/listar/em_aberto")
	public ModelAndView listarPedidosEmAberto(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEmAberto(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/listar/em_andamento")
	public ModelAndView listarPedidosEmAndamento(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEmAndamento(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/listar/prontos")
	public ModelAndView listarPedidosProntos(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarProntos(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/listar/entregues")
	public ModelAndView listarPedidosEntregues(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEntregues(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirPedido(@PathVariable("id") Long id) {
		pedidoService.excluir(id);
		return "redirect:/pedido/listar/todos";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarPedido(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		ModelAndView model = new ModelAndView("pedido/formEditarPedido");
		model.addObject("pedido", pedido);
		return model;
	}
	
	/*Função que salva o pedido modificado.
	 *Recebe uma pedido e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarPedido(@Valid Pedido pedido, BindingResult result) { 
		Pedido pedidoBanco = pedidoService.buscar(pedido.getId());
		pedidoBanco.setLocal(pedido.getLocal());
		pedidoBanco.setDinheiroCliente(pedido.getDinheiroCliente());
		pedidoBanco.setMensagem(pedido.getMensagem());
		Date today = new Date();
		pedidoBanco.setData(today);
		pedidoBanco.setStatus(Status.EM_ANDAMENTO);
		
		pedidoService.salvar(pedidoBanco);
		return "redirect:/pedido/listar/todos";
	}
	
	//Função que retorna para a página "detalhesPedido" um produto passado pela URL.
	@GetMapping(path="/detalhes_pedido/{id}")
	public ModelAndView detalhesPedido(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscar(id);
		
		//Verifica se o pedido informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(pedido == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("pedido/detalhesPedido");
		model.addObject("pedido", pedido);
		return model;
	}
	
	@PostMapping(path="/{id}/selecionar_produtos")
 	public ModelAndView adicionarProdutos(@PathVariable("id") Long id, Long id_produto, Integer quantidade) {
		Pedido pedido = pedidoService.buscar(id);
		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
		
		List<Produto> produts = new ArrayList<Produto>();
 		for(int i = 0; i < quantidade; i++) {
 			produts.add(produto);
 		}
 		
 		List<Produto> produtosJaSalvos = pedido.getProdutos();
 		produtosJaSalvos.addAll(produts);
 		
 		pedido.setPreco(pedido.getPreco() + (produto.getValorDeVenda() * quantidade));
 		pedido.setProdutos(produtosJaSalvos);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model; 
	}
	
	@PostMapping(path="/{id}/selecionar_produtos/editar")
 	public ModelAndView editarAdicionarProdutos(@PathVariable("id") Long id, Long id_produto, Integer quantidade) {
		Pedido pedido = pedidoService.buscar(id);
		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
		
		List<Produto> produts = new ArrayList<Produto>();
 		for(int i = 0; i < quantidade; i++) {
 			produts.add(produto);
 		}
 		
 		List<Produto> produtosJaSalvos = pedido.getProdutos();
 		produtosJaSalvos.addAll(produts);
 		
 		pedido.setPreco(pedido.getPreco() + (produto.getValorDeVenda() * quantidade));
 		pedido.setProdutos(produtosJaSalvos);
 		pedido.setStatus(Status.EM_ABERTO);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model; 
	}
	
	@GetMapping(path="/{id_pedido}/remover_produto/{id_produto}")
 	public ModelAndView removerProdutos(@PathVariable("id_pedido") Long id_pedido, @PathVariable("id_produto") Long id_produto) {
 		Pedido pedido = pedidoService.buscar(id_pedido);
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		
 		List<Produto> produtosDoPedido = pedido.getProdutos();
 		produtosDoPedido.remove(produto);
 		
 		pedido.setPreco(pedido.getPreco() - (produto.getValorDeVenda()));
 		pedido.setProdutos(produtosDoPedido);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model;
	}
	
	@GetMapping(path="/{id_pedido}/remover_produto/{id_produto}/editar")
 	public ModelAndView editarRemoverProdutos(@PathVariable("id_pedido") Long id_pedido, @PathVariable("id_produto") Long id_produto) {
 		Pedido pedido = pedidoService.buscar(id_pedido);
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		
 		List<Produto> produtosDoPedido = pedido.getProdutos();
 		produtosDoPedido.remove(produto);
 		
 		pedido.setPreco(pedido.getPreco() - (produto.getValorDeVenda()));
 		pedido.setProdutos(produtosDoPedido);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model;
	}
	
	@GetMapping(path="/editar_status/{id_pedido}")
	public ModelAndView editarStatusPedido(@PathVariable ("id_pedido") Long id_pedido) {
		Pedido pedido = pedidoService.buscar(id_pedido);
		
		//Verifica se o pedido existe, caso contrário o usuário é redirecionado para uma página de erro.
		if(pedido == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido não encontrado!");
			return model;
		}
		
		ModelAndView model = new ModelAndView("pedido/alterarStatus");
		model.addObject("pedido", pedido);
		return model;
	}
	
	//Função que altera o status do pedido passado no formulário e atualiza essa informação.
	@PostMapping(path="/editar_status")
	public String editarStatusPedido(@Valid Pedido pedido) {
		Pedido pedidoBanco = pedidoService.buscar(pedido.getId());
		
		if(pedido.getStatus().equals(pedidoBanco.getStatus()))
			return "redirect:/pedido/listar/todos";
		
		pedidoService.salvar(pedido);
		return "redirect:/pedido/listar/todos";
	}
}
