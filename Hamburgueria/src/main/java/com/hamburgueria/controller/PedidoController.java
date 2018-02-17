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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Pedido;
import com.hamburgueria.model.Produto;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.Status;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.PedidoService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Mensagem;

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
	
	@Autowired
	IngredienteController ingredienteController;
	
	@Autowired
	SedeService sedeService;
	
	/*Função que retorna para a página "formAdicionarLanchesProntos" uma lista de produtos disponíveis e um pedido novo.
	 * O pedido enviado vai com o status, preço e sede preenchidos previamente.
	 */
	@GetMapping(path="/lanches_prontos")
	public ModelAndView lanchesProntos() {		
		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		Pedido pedido = new Pedido();
		Date today = new Date();
		pedido.setData(today);
		pedido.setStatus(Status.EM_ABERTO);
		pedido.setPreco(0.0);
		pedido.setSede(usuarioService.usuarioLogado().getSede());
		pedido.setTroco(0.0);
		
		//Escolhe uma mensagem aleatória e adiciona ao pedido.
		Mensagem mensagem = new Mensagem();
		pedido.setMensagem(mensagem.getMensagem());
		
		Pedido pedidoSalvo = pedidoService.salvar(pedido);
		
		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoSalvo);
		return model;
	}
	
	/*Função que retorna para a página "formEditarAdicionarLanchesProntos" uma lista de produtos disponíveis
	 * e um pedido recebido por url.
	 */
	@GetMapping(path="/lanches_prontos/editar/{id}")
	public ModelAndView editarLanchesProntos(@PathVariable("id") Long id) {		
		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o pedido informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(pedido == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedido);
		return model;
	}

	//Função que retorna para a página "formCadastroPedido" um recebido por URL.
	@GetMapping(path="/cadastrar/{id}")
	public ModelAndView cadastrarPedido(@PathVariable("id") Long id) {		
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o pedido informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(pedido == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("pedido/formCadastroPedido");
		model.addObject("pedido", pedido);
		return model;
	}
	
	//Função que salva o pedido cadastrado.
	@PostMapping(path="/cadastrar")
	public String cadastrarPedido(@Valid Pedido pedido) {
		//Como o pedido foi cadastrado por um funcionários o cliente é null.
		pedido.setCliente(null);
		//Adiciona a data do pedido a data em que o pedido foi criado.
		Date today = new Date();
		pedido.setData(today);
		pedido.setPreco(pedido.getPreco());
		pedido.setStatus(Status.EM_ABERTO);
		pedido.setSede(usuarioService.usuarioLogado().getSede());
		
		//Adicionao o pedido a lista de pedidos da sede do usuário logado.
		this.adicionarPedidoSede(pedido, usuarioService.usuarioLogado().getSede());
		
		pedidoService.salvar(pedido);
		
		return "redirect:/pedido/listar/todos";
	}
	
	/*Função que retorna para a página "listarPedidos"
	 * uma lista de todos os pedidos da sede do usuário logado.
	*/
	@GetMapping(path="/listar/todos")
	public ModelAndView listarPedidos(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	/*Função que retorna para a página "listarPedidos"
	 * uma lista de todos os pedidos em aberto da sede do usuário logado.
	*/
	@GetMapping(path="/listar/em_aberto")
	public ModelAndView listarPedidosEmAberto(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEmAberto(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	/*Função que retorna para a página "listarPedidos"
	 * uma lista de todos os pedidos em andamento da sede do usuário logado.
	*/
	@GetMapping(path="/listar/em_andamento")
	public ModelAndView listarPedidosEmAndamento(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEmAndamento(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	/*Função que retorna para a página "listarPedidos"
	 * uma lista de todos os pedidos prontos da sede do usuário logado.
	*/
	@GetMapping(path="/listar/prontos")
	public ModelAndView listarPedidosProntos(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarProntos(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	/*Função que retorna para a página "listarPedidos"
	 * uma lista de todos os pedidos entregues da sede do usuário logado.
	*/
	@GetMapping(path="/listar/entregues")
	public ModelAndView listarPedidosEntregues(){
		ModelAndView model = new ModelAndView("pedido/listarPedidos");
		List<Pedido> pedidos = pedidoService.listarEntregues(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("pedidos", pedidos);		
		return model;
	}
	
	//Função que exclui um pedido.
	@GetMapping(path="/excluir/{id}")
	public String excluirPedido(@PathVariable("id") Long id, RedirectAttributes attributes) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());		
		
		//Verifica se o status era EM_ABERTO ou o papel do usuário é MASTER ou ADM, pois apenas pedidos em aberto podem ser editados.
		if(pedido.getStatus().equals(Status.EM_ABERTO) || 
				usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR) ||
				usuarioService.usuarioLogado().getPapel().equals(Papel.MASTER)) {
			
			pedidoService.excluir(id);
		}
		attributes.addFlashAttribute("mensagemExcluir", "Pedido excluído com Sucesso!");
		return "redirect:/pedido/listar/todos";
	}
	
	//Função que retorna para a página "formEditarPedido" um pedido recebido por URL.
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarPedido(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o pedido informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(pedido == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido não encontrado");
			return model;
		}
		
		//Verifica se o status era EM_ABERTO ou o papel do usuário é MASTER ou ADM, pois apenas pedidos em aberto podem ser editados.
		if(pedido.getStatus().equals(Status.EM_ABERTO) || 
				usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR) ||
				usuarioService.usuarioLogado().getPapel().equals(Papel.MASTER)) {
			ModelAndView model = new ModelAndView("pedido/formEditarPedido");
			model.addObject("pedido", pedido);
			return model;
		}
		
		ModelAndView model = new ModelAndView("erros/erro");
		model.addObject("mensagem", "Apenas pedidos abertos podem ser editados.");
		return model;
	}
	
	/*Função que salva o pedido modificado.
	 *Recebe uma pedido e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarPedido(@Valid Pedido pedido, BindingResult result) { 
		Pedido pedidoBanco = pedidoService.buscar(pedido.getId(), usuarioService.usuarioLogado().getSede().getId());
		pedidoBanco.setLocal(pedido.getLocal());
		pedidoBanco.setTroco(pedido.getTroco());
		pedidoBanco.setMensagem(pedido.getMensagem());
		Date today = new Date();
		pedidoBanco.setData(today);
		pedidoBanco.setStatus(Status.EM_ANDAMENTO);
		
		//Altera a quantidade de todos os ingredientes presentes no pedido.
		this.debitaIngredientes(pedidoBanco);
		
		pedidoService.salvar(pedidoBanco);
		
		return "redirect:/pedido/" + pedidoBanco.getId();
	}
	
	//Função que retorna para a página "detalhesPedido" um produto passado pela URL.
	@GetMapping(path="/{id}")
	public ModelAndView detalhesPedido(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
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
	
	/*Função adiciona uma lista de produtos ao pedido que está sendo criado
	 * A função recebe o id de um pedido, id de um produto e a quantidade desse produto a ser adicionada no pedido.
	 * A função retorna o pedido alterado e uma lista com o produtos disponiveis.
	 */
	@PostMapping(path="/{id}/selecionar_produtos")
 	public ModelAndView adicionarProdutos(@PathVariable("id") Long id, Long id_produto, Integer quantidade) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
		
		//Adiciona o produto a uma lista, faz isso com base na quantidade informada.
		List<Produto> produts = new ArrayList<Produto>();
 		for(int i = 0; i < quantidade; i++) {
 			produts.add(produto);
 		}
 		
 		//Concatena a lista de produtos antigos do pedido e a lista de produtos criadas acima.
 		List<Produto> produtosJaSalvos = pedido.getProdutos(); 
 		produts.addAll(produtosJaSalvos);
 		
 		//Atualiza a lista de produtos do pedido e salva o mesmo.
 		pedido.setProdutos(produts);
 		Pedido pedidoSalvo = pedidoService.salvar(pedido);
 		
 		//Verifica se tem ingrediente suficiente no estoque para fazer o pedido, caso não esse bloco é executado.
 		if(!this.temEstoque(pedidoSalvo)) {
 			//Atualiza a lista de produtos do pedido para a antiga.
 			List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 			pedidoSalvo.setProdutos(produtosJaSalvos);
 			pedidoService.salvar(pedidoSalvo);
 			
 			//Pega o pedido no banco e redireciona para a página de escolher produdo, acompanha uma mensagem de erro e a lista de produtos disponiveis.
 			Pedido pedidoBanco = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
	 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
			model.addObject("produtos", produtos);
			model.addObject("pedido", pedidoBanco);
			model.addObject("mensagem", "Desculpe não temos ingredientes suficientes para adicionar esse produto ao seu pedido.");
			return model; 
 		}

 		//Atualiza o preço do pedido e salva.
 		pedido.setPreco(pedido.getPreco() + (produto.getValorDeVenda() * quantidade));
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		//Retorna para página de escolher produto o pedido atualizado e a lista de produtos disponiveis.
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model; 
	}
	
	/*Função adiciona uma lista de produtos ao pedido que está sendo editado
	 * A função recebe o id de um pedido, id de um produto e a quantidade desse produto a ser adicionada no pedido.
	 * A função retorna o pedido alterado e uma lista com o produtos disponiveis.
	 */
	@PostMapping(path="/{id}/selecionar_produtos/editar")
 	public ModelAndView editarAdicionarProdutos(@PathVariable("id") Long id, Long id_produto, Integer quantidade) {
		Pedido pedido = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());		
		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
		
		//Adiciona o produto a uma lista, faz isso com base na quantidade informada.
		List<Produto> produts = new ArrayList<Produto>();
 		for(int i = 0; i < quantidade; i++) {
 			produts.add(produto);
 		}
 		
 		//Concatena a lista de produtos antigos do pedido e a lista de produtos criadas acima.
 		List<Produto> produtosJaSalvos = pedido.getProdutos();
 		produts.addAll(produtosJaSalvos);
 		
 		//Atualiza a lista de produtos do pedido e salva o mesmo.
 		pedido.setProdutos(produts);
 		Pedido pedidoSalvo = pedidoService.salvar(pedido);
 		
 		//Verifica se tem ingrediente suficiente no estoque para fazer o pedido, caso não esse bloco é executado.
 		if(!this.temEstoque(pedidoSalvo)) {
 			//Atualiza a lista de produtos do pedido para a antiga.
 			List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 			pedidoSalvo.setProdutos(produtosJaSalvos);
 			pedidoService.salvar(pedidoSalvo);
 			
 			//Pega o pedido no banco e redireciona para a página de escolher produdo, acompanha uma mensagem de erro e a lista de produtos disponiveis.
 			Pedido pedidoBanco = pedidoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
	 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
			model.addObject("produtos", produtos);
			model.addObject("pedido", pedidoBanco);
			model.addObject("mensagem", "Desculpe não temos ingredientes suficientes para adicionar esse produto ao seu pedido.");
			return model; 
 		}
 		
 		//Atualiza o preço do pedido e salva.
 		pedido.setPreco(pedido.getPreco() + (produto.getValorDeVenda() * quantidade));
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		//Retorna para página de escolher produto o pedido atualizado e a lista de produtos disponiveis.
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model; 
	}
	
	/*Função que remove um produto da lista de produtos de um pedido.
	 * Recebe o id de um pedido e o id de um produto.
	 * Retorna para a página "formAdicionarLanchesProntos" o pedido atualizado e uma lista de produtos disponíveis.
	 * */
	@GetMapping(path="/{id_pedido}/remover_produto/{id_produto}")
 	public ModelAndView removerProdutos(@PathVariable("id_pedido") Long id_pedido, @PathVariable("id_produto") Long id_produto) {
 		Pedido pedido = pedidoService.buscar(id_pedido, usuarioService.usuarioLogado().getSede().getId());
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		
		//Verifica se o pedido e o produto informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(pedido == null || produto == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Pedido ou Produto não encontrado");
			return model;
		}
 		
		//Remove o produto informado da lista de produtos do pedido.
 		List<Produto> produtosDoPedido = pedido.getProdutos();
 		produtosDoPedido.remove(produto);
 		
 		//Atualiza o preço do pedido e a lista de produtos do pedido.
 		pedido.setPreco(pedido.getPreco() - (produto.getValorDeVenda()));
 		pedido.setProdutos(produtosDoPedido);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model;
	}
	
	/*Função que remove um produto da lista de produtos de um pedido.
	 * Recebe o id de um pedido e o id de um produto.
	 * Retorna para a página "formAdicionarLanchesProntos" o pedido atualizado e uma lista de produtos disponíveis.
	 * */
	@GetMapping(path="/{id_pedido}/remover_produto/{id_produto}/editar")
 	public ModelAndView editarRemoverProdutos(@PathVariable("id_pedido") Long id_pedido, @PathVariable("id_produto") Long id_produto) {
 		Pedido pedido = pedidoService.buscar(id_pedido, usuarioService.usuarioLogado().getSede().getId());
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		
 		//Verifica se o pedido e o produto informado existe, caso não exista o usuário é redirecionado para uma página de erro.
 		if(pedido == null || produto == null) {
 			ModelAndView model = new ModelAndView("erros/erro");
 			model.addObject("mensagem", "Pedido ou Produto não encontrado");
 			return model;
 		}
 		
 		//Remove o produto informado da lista de produtos do pedido.
 		List<Produto> produtosDoPedido = pedido.getProdutos();
 		produtosDoPedido.remove(produto);
 		
 		//Atualiza o preço do pedido e a lista de produtos do pedido.
 		pedido.setPreco(pedido.getPreco() - (produto.getValorDeVenda()));
 		pedido.setProdutos(produtosDoPedido);
 		
 		Pedido pedidoAtualizado = pedidoService.salvar(pedido);
 		
 		List<Produto> produtos = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
 		ModelAndView model = new ModelAndView("pedido/formEditarAdicionarLanchesProntos");
		model.addObject("produtos", produtos);
		model.addObject("pedido", pedidoAtualizado);
		return model;
	}
	
	//Função que retorna para a página "alterarStatus" um pedido recebido por parametro.
	@GetMapping(path="/editar_status/{id_pedido}")
	public ModelAndView editarStatusPedido(@PathVariable ("id_pedido") Long id_pedido) {
		Pedido pedido = pedidoService.buscar(id_pedido, usuarioService.usuarioLogado().getSede().getId());
		
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
	public String editarStatusPedido(@Valid Pedido pedido, RedirectAttributes attributes) {
		Pedido pedidoBanco = pedidoService.buscar(pedido.getId(), usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o status foi alterado, caso contrário ele apenas redireciona para lista de pedidos.
		if(pedido.getStatus().equals(pedidoBanco.getStatus())) {
			return "redirect:/pedido/listar/todos";
		}
		
		//Verifica se o status antigo era EM ABERTO, caso contrário ele altera o status do pedido.
		if(!pedidoBanco.getStatus().equals(Status.EM_ABERTO)) {
			pedidoBanco.setStatus(pedido.getStatus());
			pedidoService.salvar(pedidoBanco);
			return "redirect:/pedido/listar/todos";
		//O status era EM ABERTO e tem estoque suficiente para aceitar o pedido.	
		}else if(this.temEstoque(pedidoBanco)) {
			//Debita os ingredientes, altera o status e salva.
			this.debitaIngredientes(pedidoBanco);
			pedidoBanco.setStatus(pedido.getStatus());
			pedidoService.salvar(pedidoBanco);
			return "redirect:/pedido/listar/todos";
		}
		
		attributes.addFlashAttribute("mensagemStatus", "Status atualizado com Sucesso!");
		return "redirect:/pedido/listar/todos";
	}
	
	//Função que finaliza o cadastro de um pedido.
	@PostMapping(path="/finalizar")
	public String finalizarPedido(@Valid Pedido pedido, Double dinheiroCliente, RedirectAttributes attributes) {
		Pedido pedidoBanco = pedidoService.buscar(pedido.getId(), usuarioService.usuarioLogado().getSede().getId());
		Date today = new Date();
		pedidoBanco.setData(today);
		pedidoBanco.setSede(usuarioService.usuarioLogado().getSede());
		pedidoBanco.setStatus(Status.EM_ANDAMENTO);
		pedidoBanco.setLocal(pedido.getLocal());
		pedidoBanco.setMensagem(pedido.getMensagem());
		if(dinheiroCliente != null) {
			pedidoBanco.setTroco(dinheiroCliente - pedidoBanco.getPreco());
		}
		
		//Debita os ingredientes do estoque com base na quantidade usada no pedido.
		this.debitaIngredientes(pedidoBanco);
		
		pedidoService.salvar(pedidoBanco);
		
		attributes.addFlashAttribute("mensagemCadastro", "Pedido cadastrado com Sucesso!");
		return "redirect:/pedido/" + pedidoBanco.getId();
	}
	
	//Adiciona o pedido a lista de pedidos de uma sede.
	public void adicionarPedidoSede(Pedido pedido, Sede sede) {
		List<Pedido> pedidos =  sede.getPedidos();
		
		pedidos.add(pedido);
		sede.setPedidos(pedidos);
		sedeService.salvar(sede);
	}
	
	//Conta a quantidade de um ingrediente em um produto.
	public Integer contaIngredienteProduto(Produto produto, Ingrediente ingrediente) {
		int contador = 0;
		for (Ingrediente i : produto.getIngredientes()) {
			if(i.equals(ingrediente))
				contador++;
		}
		return contador;
	}
	
	//Verifica se algum ingrediente presente no pedido tem a quantidade suficiente no estoque.
	public boolean temEstoque(Pedido pedido) {
		List<Ingrediente> ingredientes = ingredienteService.getIngredientes(pedido.getId());
		
		for (Ingrediente ingrediente : ingredientes) {
			if(pedidoService.contaIngredientes(pedido.getId(), ingrediente.getId()) > ingrediente.getQtd()) {
				return false;
			}
		}
		
		ingredienteService.getIngredientes(pedido.getId());
		return true;
	}
	
	//Conta o a quantidade máxima de produtos que pode ser pedido com base no estoque.
	public Integer contaMaximoProduto(Produto produto) {
		int maximo = produto.getIngredientes().get(0).getQtd() / this.contaIngredienteProduto(produto, produto.getIngredientes().get(0));
		for (Ingrediente ingrediente : produto.getIngredientes()) {
			if((ingrediente.getQtd() / this.contaIngredienteProduto(produto, ingrediente)) < maximo)
				maximo = ingrediente.getQtd() / this.contaIngredienteProduto(produto, ingrediente);
		}
		return maximo;
	}
	
	//Debita os ingredientes do estoque com base na quantidade usada no pedido.
	public void debitaIngredientes(Pedido pedido) {
		List<Ingrediente> ingredientes = ingredienteService.getIngredientes(pedido.getId());
		for (Ingrediente ingrediente : ingredientes) {
			ingrediente.setQtd(ingrediente.getQtd() - pedidoService.contaIngredientes(pedido.getId(), ingrediente.getId()));
			ingredienteService.salvar(ingrediente);
			ingredienteController.verificaDisponibilidade(ingrediente);
		}
	}

}
