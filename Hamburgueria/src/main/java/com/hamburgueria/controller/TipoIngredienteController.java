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
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.TipoIngredienteService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/tipo_ingrediente")
public class TipoIngredienteController {
	
	@Autowired
	TipoIngredienteService tipoIngredienteService;
	
	@Autowired
	UsuarioService usuarioService;

	@Autowired
	SedeService sedeService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarTipoIngrediente(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("tipoIngrediente/formCadastroTipoIngrediente");
		model.addObject(new TipoIngrediente());
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarTipoIngrediente(@Valid TipoIngrediente tipoIngrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		
		TipoIngrediente salvo = tipoIngredienteService.salvar(tipoIngrediente);
		
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_TIPO_INGREDIENTE);
		}
		
		salvo.setSede(usuarioService.usuarioLogado().getSede());
		Sede sede = this.adicionarTipoIngredienteSede(tipoIngrediente, usuarioService.usuarioLogado().getSede());
		sedeService.salvar(sede);
		
		tipoIngredienteService.salvar(salvo);
		
		return "redirect:/tipo_ingrediente/listar";
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarTipoIngredientes(){
		ModelAndView model = new ModelAndView("tipoIngrediente/listarTipoIngredientes");
		List<TipoIngrediente> tipoIngredientes = tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("tipoIngredientes", tipoIngredientes);
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirTipoIngrediente(@PathVariable("id") Long id) {
		TipoIngrediente tipoIngrediente = tipoIngredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if(tipoIngrediente == null)
			return "redirect:/tipo_ingrediente/listar";
		
		Sede sede = this.removerTipoIngredienteSede(tipoIngrediente, tipoIngrediente.getSede());
		sedeService.salvar(sede);
		
		tipoIngredienteService.excluir(id);
			
		return "redirect:/tipo_ingrediente/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarTipoIngrediente(@PathVariable("id") Long id) {
		TipoIngrediente tipoIngrediente = tipoIngredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if(tipoIngrediente == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Tipo Ingrediente não encontrado");
			return model;
		}			
		
		ModelAndView model = new ModelAndView("tipoIngrediente/formEditarTipoIngrediente");
		model.addObject("tipoIngrediente", tipoIngrediente);
		return model;
	}
	
	@PostMapping(path="/editar")
	public String editarTipoIngrediente(@Valid TipoIngrediente tipoIngrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		if(imagem != null && !imagem.isEmpty()) {
			tipoIngrediente.setFoto64(Image.imagemBase64(imagem));
		}
		tipoIngredienteService.salvar(tipoIngrediente);
		
		return "redirect:/tipo_ingrediente/listar";
	}
	
	public Sede adicionarTipoIngredienteSede(TipoIngrediente tipoIngrediente, Sede sede) {
		List<TipoIngrediente> tipos = sede.getTipoIngredientes();
		tipos.add(tipoIngrediente);
		sede.setTipoIngredientes(tipos);
		
		return sede;
	}
	
	public Sede removerTipoIngredienteSede(TipoIngrediente tipoIngrediente, Sede sede) {
		List<TipoIngrediente> tipos = sede.getTipoIngredientes();
		tipos.remove(tipoIngrediente);
		sede.setTipoIngredientes(tipos);
		
		return sede;
	}
	

}
