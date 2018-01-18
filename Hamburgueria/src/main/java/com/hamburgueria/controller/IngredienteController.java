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
import com.hamburgueria.service.IngredienteService;

@Controller
@RequestMapping(path="/ingrediente")
public class IngredienteController {

	@Autowired
	IngredienteService ingredienteService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarIngrediente(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("ingrediente/formCadastroIngrediente");
		model.addObject(new Ingrediente());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarIngrediente(@Valid Ingrediente ingrediente, BindingResult result) {
		//if (result.hasErrors()) return "ingrediente/formCadastroIngrediente";
		
		/* FALTA INFORMAR A SEDE */
		
		ingredienteService.salvar(ingrediente);
		
		return "redirect:/ingrediente/listar";
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarIngredientes(){
		ModelAndView model = new ModelAndView("ingrediente/listarIngredientes");
		List<Ingrediente> ingredientes = ingredienteService.listar();
		model.addObject("ingredientes", ingredientes);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirIngrediente(@PathVariable("id") Long id) {
		ingredienteService.excluir(id);
			
		return "redirect:/ingrediente/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id);
		ModelAndView model = new ModelAndView("ingrediente/formEditarIngrediente");
		model.addObject("ingrediente", ingrediente);
		return model;
	}
	
	@PostMapping(path="/editar")
	public String editarIngrediente(@Valid Ingrediente ingrediente, BindingResult result) { 
		ingredienteService.salvar(ingrediente);
		
		return "redirect:/ingrediente/listar";
	}
}
