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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.CodigoPromocional;
import com.hamburgueria.service.CodigoPromocionalService;

@Controller
@RequestMapping(path="/codigo")
public class CodigoPromocionalController {

	@Autowired
	CodigoPromocionalService codigoService;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroCodigo" um codigo com uma string codigo aletoria
	 */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarCodigo(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("codigo/formCadastroCodigo");
		CodigoPromocional codigo = new CodigoPromocional();
		//Pega e seta em codigo uma string aleatória de 6 digitos.
		codigo.setCodigo(codigo.gerarCodigo());
		model.addObject("codigo", codigo);
		
		return model;
	}
	
	//Função que salva o codigo cadastrado, recebe um codigo.
	@PostMapping(path="/cadastrar")
	public String cadastrarCodigo(@Valid CodigoPromocional codigo, BindingResult result, RedirectAttributes attributes) throws IOException {
		codigoService.salvar(codigo);
		
		attributes.addFlashAttribute("mensagemCadastro", "Cadastro realizado com Sucesso!");
		return "redirect:/codigo/listar";
	}
	
	/*Função que lista todos os codigos do banco.
	 *Retorna pra página "listarCodigo" esses codigos.
	 */
	@GetMapping(path="/listar")
	public ModelAndView listarCodigos(){
		ModelAndView model = new ModelAndView("codigo/listarCodigos");
		List<CodigoPromocional> codigos = codigoService.listar();
		model.addObject("codigos", codigos);		
		return model;
	}
	
	//Função que exclui um determinado codigo.
	@GetMapping(path="/excluir/{id}")
	public String excluirCodigo(@PathVariable("id") Long id, RedirectAttributes attributes) {
		CodigoPromocional codigo = codigoService.buscar(id);
		
		if(codigo != null) {
			codigoService.excluir(id);
			attributes.addFlashAttribute("mensagemExcluir", "Codigo excluído com Sucesso!");
		}
		
		return "redirect:/codigo/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarCodigo" um codigo informado pela URL.
	 */
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarCodigo(@PathVariable("id") Long id) {
		CodigoPromocional codigo = codigoService.buscar(id);
		
		//Verifica se o codigo informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(codigo == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Codigo não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("codigo/formEditarCodigo");
		model.addObject("codigo", codigo);
		return model;
	}
	
	/*Função que salva o codigo modificado.
	 *Recebe um codigo promocional
	 */
	@PostMapping(path="/editar")
	public String editarCodigo(@Valid CodigoPromocional codigo, BindingResult result) { 
		codigoService.salvar(codigo);
		
		return "redirect:/codigo/listar";
	}
	
	//Função que retorna para a página "detalhesCodigo" um codigo passado pela URL.
	@GetMapping(path="/{id}")
	public ModelAndView detalhesCodigo(@PathVariable("id") Long id) {
		CodigoPromocional codigo = codigoService.buscar(id);
		
		//Verifica se o codigo informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(codigo == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Codigo não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("codigo/detalhesCodigo");
		model.addObject("codigo", codigo);
		return model;
	}
	
}
