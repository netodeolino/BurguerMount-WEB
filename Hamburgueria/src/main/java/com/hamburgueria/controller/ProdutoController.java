package com.hamburgueria.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Produto;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;

@Controller
@RequestMapping(path="/produto")
public class ProdutoController {
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	IngredienteService ingredienteService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto(
				@RequestParam(value="ingredientesCarrinho", required=false) List<Ingrediente> ingredientesCarrinho,
				@RequestParam(value="ingredientesCarrinhoAntigo", required=false) List<Ingrediente> ingredientesCarrinhoAntigo
										) {
		
		List<Ingrediente> ingredientes = ingredienteService.listar();
		List<Ingrediente> ingredientesCarrinhoPage = new ArrayList<>();
		
		if (ingredientesCarrinhoAntigo != null) {
			ingredientesCarrinhoPage = ingredientesCarrinhoAntigo;
		}
		
		if (ingredientesCarrinho != null) {
			ingredientesCarrinhoPage.addAll(ingredientesCarrinho);
		}
		
		System.out.println("Carrinho antigo: "+ingredientesCarrinhoAntigo);
		System.out.println("Carrinho para adicionar: "+ingredientesCarrinho);
		System.out.println("Carrinho Page: "+ingredientesCarrinhoPage);
		
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto());
		model.addObject("ingredientes", ingredientes);
		model.addObject("ingredientesCarrinho", ingredientesCarrinhoPage);
		
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarProduto(@Valid Produto produto, BindingResult result) {
		
		Produto produtoBanco = produtoService.salvar(produto);
		
		produtoService.salvar(produtoBanco);
		
		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarProdutos(){
		ModelAndView model = new ModelAndView("produto/listarProdutos");
		List<Produto> produtos = produtoService.listar();
		model.addObject("produtos", produtos);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id);
		produtoService.salvar(produto);
		
		produtoService.excluir(id);
			
		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarProduto(@PathVariable("id") Long id) {
		List<Ingrediente> ingredientes = ingredienteService.listar();
		Produto produto = produtoService.buscar(id);
		
		ModelAndView model = new ModelAndView("produto/formEditarProduto");
		model.addObject("produto", produto);
		model.addObject("ingredientes", ingredientes);
		model.addObject("idProduto", produto.getId());
		return model;
	}
	
	@PostMapping(path="/editar")
	public String editarProduto(@Valid Produto produto, BindingResult result) {
		Produto produtoBanco = produtoService.buscar(produto.getId());
		
		produtoService.salvar(produtoBanco);
		
		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/detalhes_produto/{id}")
	public ModelAndView detalhesProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id);
		
		ModelAndView model = new ModelAndView("produto/detalhesProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	@PostMapping(path="/adicionar_ingrediente")
 	public ModelAndView adicionarIngredientes(Long id_ingrediente, Integer quantidade, @ModelAttribute(value="ingredientesCarrinho") ArrayList<Ingrediente> ingredientesCarrinho) {
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente);
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		System.out.println("Ingredientes adicionados: "+ingrs);
 		System.out.println("Ingredientes do carrinho: "+ingredientesCarrinho);
 		
 		return cadastrarProduto(ingrs, ingredientesCarrinho);
	}

}
