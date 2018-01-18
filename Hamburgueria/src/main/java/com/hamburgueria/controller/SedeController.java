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

import com.hamburgueria.model.Sede;
import com.hamburgueria.service.SedeService;

@Controller
@RequestMapping(path="/sede")
public class SedeController {

	@Autowired
	SedeService sedeService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarSede(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("sede/formCadastroSede");
		model.addObject(new Sede());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarSede(@Valid Sede sede, BindingResult result) {
		//if (result.hasErrors()) return "sede/formCadastroSede";
		
		sedeService.salvar(sede);
		
		return "redirect:/sede/listar";
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarSedes(){
		ModelAndView model = new ModelAndView("sede/listarSedes");
		List<Sede> sedes = sedeService.listar();
		model.addObject("sedes", sedes);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirSede(@PathVariable("id") Long id) {
		sedeService.excluir(id);
			
		return "redirect:/sede/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarSede(@PathVariable("id") Long id) {
		Sede sede = sedeService.buscar(id);
		ModelAndView model = new ModelAndView("sede/formEditarSede");
		model.addObject("sede", sede);
		return model;
	}
	
	@PostMapping(path="/editar")
	public String atualizar(@Valid Sede sede, BindingResult result) { 
		sedeService.salvar(sede);
		
		return "redirect:/sede/listar";
	}
}
