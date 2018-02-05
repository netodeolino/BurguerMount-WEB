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
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.SedeService;
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
	IngredienteController ingredienteController;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto() {		
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto(0.0, 0.0, true));

		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto(@Valid Produto produto, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		produto.setSede(usuarioService.usuarioLogado().getSede());
		
		if (imagem != null && !imagem.isEmpty()) {
			produto.setFoto64(Image.imagemBase64(imagem));
		} else {
			produto.setFoto64(Constants.IMAGE_DEFAULT_PRODUTO);
		}
		
		Produto produtoBanco = produtoService.salvar(produto);
		this.adicionarProdutoSede(produtoBanco, usuarioService.usuarioLogado().getSede());
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	public ModelAndView cadastrarProduto(Produto produto) {
		produto.setSede(usuarioService.usuarioLogado().getSede());
		Produto produtoBanco = produtoService.buscar(produto.getId(), usuarioService.usuarioLogado().getSede().getId());
		this.adicionarProdutoSede(produtoBanco, usuarioService.usuarioLogado().getSede());
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarProdutos(){
		ModelAndView model = new ModelAndView("produto/listarProdutos");
		List<Produto> disponiveis = produtoService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		List<Produto> indisponiveis = produtoService.listarIndisponiveis(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("disponiveis", disponiveis);
		model.addObject("indisponiveis", indisponiveis);
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if(produto == null) {
			return "redirect:/produto/listar";
		}
		this.removerProdutoSede(produto, produto.getSede());
		
		List<Ingrediente> ingredientes = produto.getIngredientes();
		for (Ingrediente ingrediente : ingredientes) {
			this.excluirProdutoIngrediente(produto, ingrediente);
		}
		
		produtoService.excluir(id);
			
		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		if(produto == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Produto não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("produto/formEditarProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	@PostMapping(path="/editar")
	public ModelAndView editarProduto(@Valid Produto produto, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		Produto produtoBanco = produtoService.buscar(produto.getId(), usuarioService.usuarioLogado().getSede().getId());
		
		if (imagem != null && !imagem.isEmpty()) {
			produtoBanco.setFoto64(Image.imagemBase64(imagem));
		}
		produtoBanco.setNome(produto.getNome());
		
		produtoBanco = produtoService.salvar(produtoBanco);
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formEditarAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	public ModelAndView editarProduto(Produto produto) {
		Produto produtoBanco = produtoService.buscar(produto.getId(), usuarioService.usuarioLogado().getSede().getId());
		
		List<Ingrediente> ingredientes = ingredienteService.listarTodos(usuarioService.usuarioLogado().getSede().getId());
		
		ModelAndView model = new ModelAndView("produto/formEditarAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
	}
	
	@GetMapping(path="/detalhes_produto/{id}")
	public ModelAndView detalhesProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		
		if(produto == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Produto não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("produto/detalhesProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	@PostMapping(path="/{id}/selecionar_ingredientes")
 	public ModelAndView adicionarIngredientes(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());

		if (!(ingrediente.isDisponivel())) {
			produto.setDisponivel(false);
		}
		
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		List<Ingrediente> ingredientesJaSalvos = produto.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		produto.setIngredientes(ingredientesJaSalvos);
 		produto.setValorBruto(produto.getValorBruto() + (ingrediente.getValorBruto() * quantidade));
 		produto.setValorDeVenda(produto.getValorDeVenda() + (ingrediente.getValorDeVenda() * quantidade));
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		this.adicionarProdutoIngrediente(produtoAtualizado, ingrediente);
 		ingredienteController.verificaDisponibilidade(ingrediente);
		
 		return cadastrarProduto(produtoAtualizado);
	}
	
	@GetMapping(path="/{id_produto}/remover_ingrediente/{id_ingrediente}")
 	public ModelAndView removerIngredientes(@PathVariable("id_produto") Long id_produto, @PathVariable("id_ingrediente") Long id_ingrediente) {
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
 		
 		List<Ingrediente> ingredientesDoProduto = produto.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		produto.setIngredientes(ingredientesDoProduto);
 		produto.setValorBruto(produto.getValorBruto() - ingrediente.getValorBruto());
 		produto.setValorDeVenda(produto.getValorDeVenda() - ingrediente.getValorDeVenda());
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		this.removerProdutoIngrediente(produtoAtualizado, ingrediente);
 		ingredienteController.verificaDisponibilidade(ingrediente);
 		
 		return cadastrarProduto(produtoAtualizado);
	}
	
	@GetMapping(path="/finalizar_produto")
 	public String adicionarIngredientes() {
 		return "redirect:/produto/listar";
	}
	
	@PostMapping(path="/{id}/selecionar_ingredientes/editar")
 	public ModelAndView adicionarIngredientesEditar(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) {
		Produto produto = produtoService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
		
		if (!(ingrediente.isDisponivel())) {
			produto.setDisponivel(false);
		}
		
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		List<Ingrediente> ingredientesJaSalvos = produto.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		produto.setIngredientes(ingredientesJaSalvos);
 		produto.setValorBruto(produto.getValorBruto() + (ingrediente.getValorBruto() * quantidade));
 		produto.setValorDeVenda(produto.getValorDeVenda() + (ingrediente.getValorDeVenda() * quantidade));
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		this.adicionarProdutoIngrediente(produtoAtualizado, ingrediente);
 		ingredienteController.verificaDisponibilidade(ingrediente);
		
 		return editarProduto(produtoAtualizado);
	}
	
	@GetMapping(path="/{id_produto}/remover_ingrediente/{id_ingrediente}/editar")
 	public ModelAndView removerIngredientesEditar(@PathVariable("id_produto") Long id_produto, @PathVariable("id_ingrediente") Long id_ingrediente) {
 		Produto produto = produtoService.buscar(id_produto, usuarioService.usuarioLogado().getSede().getId());
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente, usuarioService.usuarioLogado().getSede().getId());
 		
 		List<Ingrediente> ingredientesDoProduto = produto.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		produto.setIngredientes(ingredientesDoProduto);
 		produto.setValorBruto(produto.getValorBruto() - ingrediente.getValorBruto());
 		produto.setValorDeVenda(produto.getValorDeVenda() - ingrediente.getValorDeVenda());
 		
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		this.removerProdutoIngrediente(produtoAtualizado, ingrediente);
 		ingredienteController.verificaDisponibilidade(ingrediente);
 		
 		return editarProduto(produtoAtualizado);
	}
	
	public void adicionarProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		if(!produtos.contains(produto)) {
			produtos.add(produto);
			ingrediente.setProdutos(produtos);
			ingredienteService.salvar(ingrediente);
		}
	}
	
	public void removerProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		
		if(produtoService.contaIngrediente(produto.getId(), ingrediente.getId()) == 1) {
			produtos.remove(produto);
			ingrediente.setProdutos(produtos);
				
			ingredienteService.salvar(ingrediente);
		}
	}
	
	public void excluirProdutoIngrediente(Produto produto, Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		
		if(produtos.contains(produto)) {
			produtos.remove(produto);
			ingrediente.setProdutos(produtos);
			ingredienteService.salvar(ingrediente);
		}
	}
	
	public void adicionarProdutoSede(Produto produto, Sede sede) {
		List<Produto> produtos = sede.getProdutos();
		if(!produtos.contains(produto)) {
			produtos.add(produto);
			sede.setProdutos(produtos);
			
			sedeService.salvar(sede);
		}
	}
	
	public void removerProdutoSede(Produto produto, Sede sede) {
		List<Produto> produtos = sede.getProdutos();
		produtos.remove(produto);
		sede.setProdutos(produtos);
		
		sedeService.salvar(sede);
	}
}
