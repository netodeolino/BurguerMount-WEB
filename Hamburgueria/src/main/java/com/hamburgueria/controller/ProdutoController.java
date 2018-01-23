package com.hamburgueria.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import com.hamburgueria.model.Produto;
import com.hamburgueria.model.ProdutoIngrediente;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoIngredienteService;
import com.hamburgueria.service.ProdutoService;

@Controller
@RequestMapping(path="/produto")
public class ProdutoController {
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	ProdutoIngredienteService produtoIngredienteService;
	
	List<ProdutoIngrediente> ingredientesDoProduto = new ArrayList<ProdutoIngrediente>();
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto(HttpServletRequest request) {
		List<Ingrediente> ingredientes = ingredienteService.listar();
		
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto());
		model.addObject(new ProdutoIngrediente());
		model.addObject("ingredientes", ingredientes);
		model.addObject("produtoIngredientes", ingredientesDoProduto);
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarProduto(@Valid Produto produto, BindingResult result) {
		
		Produto produtoBanco = produtoService.salvar(produto);
		
		for (ProdutoIngrediente produtoIngrediente : ingredientesDoProduto) {
			produtoIngrediente.setProduto(produtoBanco);
			produtoIngredienteService.salvar(produtoIngrediente);
		}
		this.ingredientesDoProduto.clear();
		
		List<ProdutoIngrediente> ingredientesDoProduto = produtoIngredienteService.listar(produtoBanco);
		produtoBanco.setProdutoIngredientes(ingredientesDoProduto);
		
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
		produtoService.excluir(id);
			
		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarProduto(@PathVariable("id") Long id) {
		Produto produto = produtoService.buscar(id);
		ModelAndView model = new ModelAndView("produto/formEditarProduto");
		model.addObject("produto", produto);
		return model;
	}
	
	@PostMapping(path="/editar")
	public String editarProduto(@Valid Produto produto, BindingResult result) { 
		produtoService.salvar(produto);
		
		return "redirect:/produto/listar";
	}
	
	@PostMapping(path="/adicionar_ingrediente")
	public String adicionarIngredientes(@Valid ProdutoIngrediente produtoIngrediente, Long id_ingrediente) {
		Ingrediente ingredienteBanco = ingredienteService.buscar(id_ingrediente);

		produtoIngrediente.setIngrediente(ingredienteBanco);
		this.ingredientesDoProduto.add(produtoIngrediente);
		
		return "redirect:/produto/cadastrar";
	}
	
	@GetMapping(path="/remover_ingrediente/{id}")
	public String removerIngredientes(@PathVariable("id") Long id) {
		int index = -1;
		for (ProdutoIngrediente produtoIngrediente : this.ingredientesDoProduto) {
			if (produtoIngrediente.getIngrediente().getId() == id) {
				index = this.ingredientesDoProduto.indexOf(produtoIngrediente);
			}
		}
		
		if (index != -1) {
			this.ingredientesDoProduto.remove(index);
		}
		
		return "redirect:/produto/cadastrar";
	}

}
