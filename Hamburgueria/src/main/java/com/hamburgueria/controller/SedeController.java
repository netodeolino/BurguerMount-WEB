package com.hamburgueria.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hamburgueria.model.Sede;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

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
	public String cadastrarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		//if (result.hasErrors()) return "sede/formCadastroSede";
		
		Sede salva = sedeService.salvar(sede);
		
		if (imagem != null && !imagem.isEmpty()) {
			salva.setFoto64(Image.imagemBase64(imagem));
		} else {
			salva.setFoto64(Constants.IMAGE_DEFAULT_SEDE);
		}
		sedeService.salvar(salva);
		
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
	public String editarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		if(imagem != null && !imagem.isEmpty()) {
			sede.setFoto64(Image.imagemBase64(imagem));
		}
		sedeService.salvar(sede);
		
		return "redirect:/sede/listar";
	}
}
