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

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.TipoIngredienteService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/ingrediente")
public class IngredienteController {

	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	TipoIngredienteService tipoIngredienteService;
	
	@Autowired
	ProdutoService produtoService;
	
	@Autowired
	UsuarioService usuarioService;
	
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarIngrediente(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("ingrediente/formCadastroIngrediente");
		model.addObject(new Ingrediente());
		model.addObject("tipos", tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId()));
		return model;
	}
	
	@GetMapping(path="/{id_tipo}/cadastrar")
	public ModelAndView cadastrarIngredienteComTipo(@PathVariable("id_tipo") Long id_tipo, HttpServletRequest request) {
		TipoIngrediente tipo = tipoIngredienteService.buscar(id_tipo, usuarioService.usuarioLogado().getSede().getId());
		
		Ingrediente ingrediente =  new Ingrediente();
		ingrediente.setTipoIngrediente(tipo);
		
		ModelAndView model = new ModelAndView("ingrediente/formCadastroIngrediente");
		model.addObject(ingrediente);
		model.addObject("tipos", tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId()));
		return model;
	}
	
	@PostMapping(path="/cadastrar")
	public String cadastrarIngrediente(@Valid Ingrediente ingrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		Ingrediente salvo = ingredienteService.salvar(ingrediente);
		
		TipoIngrediente tipo = tipoIngredienteService.buscar(salvo.getTipoIngrediente().getId(), usuarioService.usuarioLogado().getSede().getId());
		tipo = this.adicionarIngredienteTipo(salvo, tipo);
		tipoIngredienteService.salvar(tipo);
		
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_INGREDIENTE);
		}
		ingredienteService.salvar(salvo);
		
		return "redirect:/ingrediente/" + tipo.getId() + "/listar";
	}
	
	@GetMapping(path="/listar")
	public ModelAndView listarIngredientes(){
		ModelAndView model = new ModelAndView("ingrediente/listarIngredientes");
		List<Ingrediente> ingredientes = ingredienteService.listar();
		model.addObject("ingredientes", ingredientes);		
		return model;
	}
	
	@GetMapping(path="/{id_tipo}/listar")
	public ModelAndView listarIngredientesPorTipo(@PathVariable("id_tipo") Long id_tipo){
		ModelAndView model = new ModelAndView("ingrediente/listarIngredientes");
		
		TipoIngrediente tipo = tipoIngredienteService.buscar(id_tipo, usuarioService.usuarioLogado().getSede().getId());
		List<Ingrediente> ingredientes = tipo.getIngredientes();
		
		model.addObject("tipo", tipo);
		model.addObject("ingredientes", ingredientes);		
		return model;
	}
	
	@GetMapping(path="/excluir/{id}")
	public String excluirIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id);
				
		TipoIngrediente tipo = this.removerIngredienteTipo(ingrediente, ingrediente.getTipoIngrediente());
		tipoIngredienteService.salvar(tipo);
			
		ingredienteService.excluir(id);
		return "redirect:/ingrediente/listar";
	}
	
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id);
		ModelAndView model = new ModelAndView("ingrediente/formEditarIngrediente");
		model.addObject("ingrediente", ingrediente);
		model.addObject("tipos", tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId()));
		return model;
	}
	
	@PostMapping(path="/editar")
	public String editarIngrediente(@Valid Ingrediente ingrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		if(imagem != null && !imagem.isEmpty()) {
			ingrediente.setFoto64(Image.imagemBase64(imagem));
		}
		
		Ingrediente antigo = ingredienteService.buscar(ingrediente.getId());
		if(!antigo.getTipoIngrediente().equals(ingrediente.getTipoIngrediente())) {
			TipoIngrediente tipo = this.removerIngredienteTipo(antigo, antigo.getTipoIngrediente());
			tipoIngredienteService.salvar(tipo);
			
			tipo = tipoIngredienteService.buscar(ingrediente.getTipoIngrediente().getId(), usuarioService.usuarioLogado().getSede().getId());
			tipo = this.adicionarIngredienteTipo(ingrediente, tipo);
			tipoIngredienteService.salvar(tipo);
		}
		ingredienteService.salvar(ingrediente);
		
		return "redirect:/ingrediente/listar";
	}
	
	public TipoIngrediente adicionarIngredienteTipo(Ingrediente ingrediente, TipoIngrediente tipo) {
		List<Ingrediente> ingredientes = tipo.getIngredientes();
		ingredientes.add(ingrediente);
		
		tipo.setIngredientes(ingredientes);
		return tipo;
	}
	
	public TipoIngrediente removerIngredienteTipo(Ingrediente ingrediente, TipoIngrediente tipo) {
		List<Ingrediente> ingredientes = tipo.getIngredientes();
		ingredientes.remove(ingrediente);
		
		tipo.setIngredientes(ingredientes);
		return tipo;
	}
	
	@GetMapping(path="/detalhes_ingrediente/{id}")
	public ModelAndView detalhesIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id);
		
		ModelAndView model = new ModelAndView("ingrediente/detalhesIngrediente");
		model.addObject("ingrediente", ingrediente);
		return model;
	}
}
