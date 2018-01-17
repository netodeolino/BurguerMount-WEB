package com.hamburgueria.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Empresa;
import com.hamburgueria.service.EmpresaService;

@Controller
@RequestMapping(path="/empresa")
public class EmpresaController {
	
	@Autowired
	EmpresaService empresaService;
	
	@GetMapping("/cadastrar")
	public ModelAndView cadastrarEmpresa() {
		ModelAndView model = new ModelAndView("empresa/formCadastrarEmpresa");
		model.addObject("empresa", new Empresa());
		return model;
	}

	@PostMapping("/cadastrar")
	public String salvarEmpresa(@Valid Empresa empresa, BindingResult result ) {
		if (result.hasErrors()) return "redirect:/empresa/cadastrar";
		
		Empresa empresaSalva = empresaService.salvar(empresa);
		return "redirect:/empresa/"+ empresaSalva.getId();
	}
	
	@GetMapping("/deletar/{id}")
	public String deletarEmpresa(@PathVariable("id") Long id) {
		Empresa empresa = empresaService.buscar(id);
		
		empresaService.excluir(empresa.getId());
		return "redirect:/empresa/cadastrar";
	}
	
	@GetMapping("/editar/{id}")
	public ModelAndView editarEmpresa(@PathVariable("id") Long id) {
		Empresa empresa = empresaService.buscar(id);
		ModelAndView model = new ModelAndView("empresa/formEditarEmpresa");
		model.addObject("empresa", empresa);
		return model;
	}
	
	@PostMapping("/editar")
	public String editarEmpresa(@Valid Empresa empresa, BindingResult result) {	
		empresaService.salvar(empresa);
		return "redirect:/empresa/"+ empresa.getId();
	}
	
	@GetMapping("/{id}")
	public ModelAndView visualizarEmpresa(@PathVariable("id") Long id) {
		Empresa empresa = empresaService.buscar(id);
		ModelAndView model = new ModelAndView("empresa/detalhesEmpresa");
		model.addObject("empresa", empresa);
		
		return model;
	}
}
