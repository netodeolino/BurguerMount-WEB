package com.hamburgueria.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Produto;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.TipoIngredienteService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/produto")
public class ProdutoController {
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	SedeService sedeService;
	
	@Autowired
	TipoIngredienteService tipoIngredienteService;
	
	@Autowired
	IngredienteController ingredienteController;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroProduto" um novo produto já setando valorDeVenda e valorBruto como 0.
	 * */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto() {		
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto(0.0, 0.0, true));

		return model;
	}
	
	/*Função que salva o produto cadastrado.
	 *Recebe um produto e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto(@Valid Produto produto, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		//Adiciona a sede do usuário logado ao produto.
		produto.setSede(usuarioService.usuarioLogado().getSede());
		
		//Verifica se foi informada uma imagem, caso não: o produto é salvo com uma imagem padrão.
		if (produto.getFoto64() == null) {
			if (imagem != null && !imagem.isEmpty()) {
				produto.setFoto64(Image.imagemBase64(imagem));
			} else {
				produto.setFoto64(Constants.IMAGE_DEFAULT_PRODUTO);
			}
		}
		
		Produto produtoBanco = produtoService.salvar(produto);
		//Adiciona o produto a lista de produtos da sede do usuário logado.
		this.adicionarProdutoSede(produtoBanco, usuarioService.usuarioLogado().getSede());
		
		List<TipoIngrediente> tipos = tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("tipos", tipos);
		
		return model;
	}
	
	/*Função que lista todos produtos do banco (todos da sede do usuário logado).
	 *Retorna pra página "listarProdutos" esses rpodutos divididos em disponíveis e indisponiveis.
	 */
	@GetMapping(path="/listar")
	public ModelAndView listarProdutos(){
		ModelAndView model = new ModelAndView("produto/listarProdutos");
		List<Produto> disponiveis = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		List<Produto> indisponiveis = produtoService.listarIndisponiveis(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("disponiveis", disponiveis);
		model.addObject("indisponiveis", indisponiveis);
		return model;
	}
	
	//Função que exclui um determinado produto.
	@GetMapping(path="/excluir/{id}")
	public String excluirProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if(produto == null) {
			return "redirect:/produto/listar";
		}
		//Remove o produto que será excluido da lista de produtos da sua sede, e atualiza  a sede.
		this.removerProdutoSede(produto, produto.getSede());
		
		//Para cada ingrediente do produto, remove o produto da lista de produtos do ingrediente e atualiza o ingrediente.
		List<Ingrediente> ingredientes = produto.getIngredientes();
		for (Ingrediente ingrediente : ingredientes) {
			this.excluirProdutoIngrediente(produto, ingrediente);
		}
		
		produtoService.excluir(id);
		return "redirect:/produto/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarProduto" um produto informado pela URL.
	 * */
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o produto informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(produto == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Produto não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("produto/formEditarProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	/*Função que salva o produto modificado.
	 *Recebe um produto e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public ModelAndView editarProduto(@Valid Produto produto, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		Produto produtoBanco = produtoService.buscar(produto.getId(), usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if (imagem != null && !imagem.isEmpty()) {
			produtoBanco.setFoto64(Image.imagemBase64(imagem));
		}
		
		produtoBanco.setNome(produto.getNome());
		produtoBanco = produtoService.salvar(produtoBanco);
		
		List<TipoIngrediente> tipos = tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formEditarAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("tipos", tipos);
		
		return model;
	}
	
	//Função que retorna para a página "detalhesProduto" um produto passado pela URL.
	@GetMapping(path="/detalhes_produto/{id}")
	public ModelAndView detalhesProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica se o produto informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(produto == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Produto não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("produto/detalhesProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	/*Função que adiciona um determinado ingrediente a um determinado produto informado pela URL.
	 *A função recebe o id do produto, o id do ingrediente e a quantidade desse ingrediente que deve ser adicionado ao produto.
	 */
	@PostMapping(path="/{id}/selecionar_ingredientes")
 	public ModelAndView adicionarIngredientes(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) throws IOException {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica a disponibilidade do ingrediente.
		if (!(ingrediente.isDisponivel())) {
			produto.setDisponivel(false);
		}
		
		//Adiciona a uma lista de ingredientes o ingrediente passado por parâmetro, quantas vezes foi informado pela quantidade.
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		//Adiciona esses ingredientes aos ingredientes que já existiam em produto.
 		List<Ingrediente> ingredientesJaSalvos = produto.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		//Atualiza os valores de produto.
 		produto.setIngredientes(ingredientesJaSalvos);
 		produto.setValorBruto(produto.getValorBruto() + (ingrediente.getValorBruto() * quantidade));
 		produto.setValorDeVenda(produto.getValorDeVenda() + (ingrediente.getValorDeVenda() * quantidade));
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		//Adiciona o produto a lista de produtos do ingrediente informado.
 		this.adicionarProdutoIngrediente(produtoAtualizado, ingrediente);
 		
 		//Verifica se ao adicionar o ingrediente ao produto, se o produto vai continuar disponível.
 		ingredienteController.verificaDisponibilidade(ingrediente);
 		
 		return cadastrarProduto(produtoAtualizado, null);
	}
	
	/*Função para retirar um ingrediente da lista de ingrediente de um produto
	 *A função recebe um produto e um ingrediente por URL.
	 */
	@GetMapping(path="/{id_produto}/remover_ingrediente/{id_ingrediente}")
 	public ModelAndView removerIngredientes(@PathVariable("id_produto") Long id_produto, @PathVariable("id_ingrediente") Long id_ingrediente) throws IOException {
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
 		
 		//Remove o ingrediente da lista de ingredientes do produto.
 		List<Ingrediente> ingredientesDoProduto = produto.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		//Atualiza os valores do produto.
 		produto.setIngredientes(ingredientesDoProduto);
 		produto.setValorBruto(produto.getValorBruto() - ingrediente.getValorBruto());
 		produto.setValorDeVenda(produto.getValorDeVenda() - ingrediente.getValorDeVenda());
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		//Remove o produto da lista de produtos do ingrediente e atualiza o ingrediente.
 		this.removerProdutoIngrediente(produtoAtualizado, ingrediente);
 		
 		//Verifica se ao adicionar o ingrediente ao produto, se o produto vai continuar disponível.
 		ingredienteController.verificaDisponibilidade(ingrediente);
 		
 		return cadastrarProduto(produtoAtualizado, null);
	}
	
	//Função apenas para finalizar o processo de cadastro do produto.
	@GetMapping(path="/finalizar_produto")
 	public String adicionarIngredientes() {
 		return "redirect:/produto/listar";
	}
	
	/*Função de edição.
	 *Função para adicionar um ingrediente a lista de ingredientes do produto.
	 *Recebe um produto por URL, um ingrediente e a quantidade por parâmetro.
	 */
	@PostMapping(path="/{id}/selecionar_ingredientes/editar")
 	public ModelAndView adicionarIngredientesEditar(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) throws IOException {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
		
		//Verifica a disponibilidade do ingrediente.
		if (!(ingrediente.isDisponivel())) {
			produto.setDisponivel(false);
		}
		
		//Adiciona a uma lista de ingredientes o ingrediente passado por parâmetro, quantas vezes foi informado pela quantidade.
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		//Adiciona esses ingredientes aos ingredientes que já existiam em produto.
 		List<Ingrediente> ingredientesJaSalvos = produto.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		//Atualiza os valores de produto.
 		produto.setIngredientes(ingredientesJaSalvos);
 		produto.setValorBruto(produto.getValorBruto() + (ingrediente.getValorBruto() * quantidade));
 		produto.setValorDeVenda(produto.getValorDeVenda() + (ingrediente.getValorDeVenda() * quantidade));
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		//Adiciona o produto a lista de produtos do ingrediente informado.
 		this.adicionarProdutoIngrediente(produtoAtualizado, ingrediente);
 		
 		//Verifica se ao adicionar o ingrediente ao produto, se o produto vai continuar disponível
 		ingredienteController.verificaDisponibilidade(ingrediente);
		
 		return editarProduto(produtoAtualizado, null);
	}
	
	/*Função de edição.
	 *Função para retirar um ingrediente da lista de ingrediente de um produto
	 *A função recebe um produto e um ingrediente por URL.
	 */
	@GetMapping(path="/{id_produto}/remover_ingrediente/{id_ingrediente}/editar")
 	public ModelAndView removerIngredientesEditar(@PathVariable("id_produto") Long id_produto, @PathVariable("id_ingrediente") Long id_ingrediente) throws IOException {
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
 		
 		//Remove o ingrediente da lista de ingredientes do produto.
 		List<Ingrediente> ingredientesDoProduto = produto.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		//Atualiza os valores do produto.
 		produto.setIngredientes(ingredientesDoProduto);
 		produto.setValorBruto(produto.getValorBruto() - ingrediente.getValorBruto());
 		produto.setValorDeVenda(produto.getValorDeVenda() - ingrediente.getValorDeVenda());
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		//Remove o produto da lista de produtos do ingrediente e atualiza o ingrediente.
 		this.removerProdutoIngrediente(produtoAtualizado, ingrediente);
 		
 		//Verifica se ao adicionar o ingrediente ao produto, se o produto vai continuar disponível.
 		ingredienteController.verificaDisponibilidade(ingrediente);
 		
 		return editarProduto(produtoAtualizado, null);
	}
	
	//Adiciona um produto a lista de produtos de um ingrediente e atualiza o ingrediente.
	public void adicionarProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		if(!produtos.contains(produto)) {
			produtos.add(produto);
			ingrediente.setProdutos(produtos);
			ingredienteService.salvar(ingrediente);
		}
	}
	
	//Remove um produto da lista de produtos de um ingrediente e atualiza o ingrediente caso haja apenas esse ingrediente.
	public void removerProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		
		if(produtoService.contaIngrediente(produto.getId(), ingrediente.getId()) == 1) {
			produtos.remove(produto);
			ingrediente.setProdutos(produtos);
				
			ingredienteService.salvar(ingrediente);
		}
	}
	
	//Remove um produto da lista de produtos de um ingrediente e atualiza o ingrediente.
	public void excluirProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		
		if(produtos.contains(produto)) {
			produtos.remove(produto);
			ingrediente.setProdutos(produtos);
			ingredienteService.salvar(ingrediente);
		}
	}
	
	//Adiciona um produto a lista de produtos de uma sede e atualiza a sede.
	public void adicionarProdutoSede(Produto produto, Sede sede) {
		List<Produto> produtos = sede.getProdutos();
		if(!produtos.contains(produto)) {
			produtos.add(produto);
			sede.setProdutos(produtos);
			
			sedeService.salvar(sede);
		}
	}
	
	//Remove um produto da lista de produtos de uma sede e atualiza a sede.
	public void removerProdutoSede(Produto produto, Sede sede) {
		List<Produto> produtos = sede.getProdutos();
		produtos.remove(produto);
		sede.setProdutos(produtos);
		
		sedeService.salvar(sede);
	}
	
}
