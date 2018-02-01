package com.hamburgueria.controller;

import java.util.ArrayList;
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
import com.hamburgueria.model.Produto;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.UsuarioService;

@Controller
@RequestMapping(path="/produto")
public class ProdutoController {
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto() {		
		ModelAndView model = new ModelAndView("produto/formCadastroProduto");
		model.addObject(new Produto());

		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public ModelAndView cadastrarProduto(@Valid Produto produto) {
		produto.setSede(usuarioService.usuarioLogado().getSede());
		Produto produtoBanco = produtoService.salvar(produto);
		
		List<Ingrediente> ingredientes = ingredienteService.listar();
		
		ModelAndView model = new ModelAndView("produto/formAdicionarIngredientes");
		model.addObject("produto", produtoBanco);
		model.addObject("ingredientes", ingredientes);
		
		return model;
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
	
	@PostMapping(path="/{id}/selecionar_ingredientes")
 	public ModelAndView adicionarIngredientes(@PathVariable("id") Long id, Long id_ingrediente, Integer quantidade) {
		Produto produto = produtoService.buscar(id);
		
		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente);
		List<Ingrediente> ingrs = new ArrayList<Ingrediente>();
 		
 		for(int i = 0; i < quantidade; i++) {
 			ingrs.add(ingrediente);
 		}
 		
 		List<Ingrediente> ingredientesJaSalvos = produto.getIngredientes();
 		ingredientesJaSalvos.addAll(ingrs);
 		
 		produto.setIngredientes(ingredientesJaSalvos);
 		Produto produtoAtualizado = produtoService.salvar(produto);
		
 		return cadastrarProduto(produtoAtualizado);
	}
	
	@GetMapping(path="/finalizar_produto")
 	public String adicionarIngredientes() {
 		return "redirect:/produto/listar";
	}
	
	@GetMapping(path="/{id_produto}/remover_ingrediente/{id_ingrediente}")
 	public ModelAndView removerIngredientes(@PathVariable("id_produto") Long id_produto, @PathVariable("id_ingrediente") Long id_ingrediente) {
 		Produto produto = produtoService.buscar(id_produto);
 		Ingrediente ingrediente = ingredienteService.buscar(id_ingrediente);
 		
 		List<Ingrediente> ingredientesDoProduto = produto.getIngredientes();
 		ingredientesDoProduto.remove(ingrediente);
 		
 		produto.setIngredientes(ingredientesDoProduto);
 		Produto produtoAtualizado = produtoService.salvar(produto);
 		
 		return cadastrarProduto(produtoAtualizado);
	}

}
