package com.hamburgueria.controller;

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
	public ModelAndView cadastrarProduto(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarProduto(@Valid Produto produto, BindingResult result) {
		produto.setIngredientes(null);
		produtoService.salvar(produto);
		
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
	
	@GetMapping(path="/adicionarIngredientes")
	public ModelAndView adicionarIngredientes() {
		List<Ingrediente> ingredientes = ingredienteService.listar();
		ModelAndView model = new ModelAndView("produto/listaAdicionarIngredientes");
		model.addObject("ingredientes", ingredientes);
		model.addObject("produto", new Produto());
		
		return model;
	}

}
