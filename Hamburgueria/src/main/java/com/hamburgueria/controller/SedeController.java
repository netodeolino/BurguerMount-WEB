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

import com.hamburgueria.model.Papel;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.service.SedeService;
import com.hamburgueria.service.UsuarioService;
import com.hamburgueria.util.Constants;
import com.hamburgueria.util.Image;

@Controller
@RequestMapping(path="/sede")
public class SedeController {

	@Autowired
	SedeService sedeService;
	
	@Autowired
	UsuarioService usuarioService;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroSede" uma sede vazia e uma lista de  sedes
	 */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarSede(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("sede/formCadastroSede");
		model.addObject(new Sede());
		return model;
	}
	
	/*Função que salva a sede cadastrada.
	 *Recebe uma sede e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public String cadastrarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		Sede salva = sedeService.salvar(sede);
		
		//Verifica se foi informada uma imagem, caso não: o ingrediente é salvo com uma imagem padrão.
		if (imagem != null && !imagem.isEmpty()) {
			salva.setFoto64(Image.imagemBase64(imagem));
		} else {
			salva.setFoto64(Constants.IMAGE_DEFAULT_SEDE);
		}
		
		sedeService.salvar(salva);
		
		return "redirect:/sede/listar";
	}
	
	/*Função que lista todas sedes do banco.
	 *Retorna pra página "listarSedes" essas sedes.
	 */
	@GetMapping(path="/listar")
	public ModelAndView listarSedes(){
		ModelAndView model = new ModelAndView("sede/listarSedes");
		List<Sede> sedes = sedeService.listar();
		model.addObject("sedes", sedes);		
		return model;
	}
	
	//Função que exclui uma determinada sede.
	@GetMapping(path="/excluir/{id}")
	public String excluirSede(@PathVariable("id") Long id) {
		Sede sede = sedeService.buscar(id);
		
		//Remove a sede do usuário e atualiza o usuário.
		this.removerSedeUsuarios(sede);
		sedeService.excluir(id);
		
		return "redirect:/sede/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarSede" uma sede informada pela URL.
	 */
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarSede(@PathVariable("id") Long id) {
		Sede sede = sedeService.buscar(id);
		ModelAndView model = new ModelAndView("sede/formEditarSede");
		model.addObject("sede", sede);
		return model;
	}
	
	/*Função que salva a sede modificada.
	 *Recebe uma sede e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarSede(@Valid Sede sede, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if(imagem != null && !imagem.isEmpty()) {
			sede.setFoto64(Image.imagemBase64(imagem));
		}
		sedeService.salvar(sede);
		
		return "redirect:/sede/listar";
	}
	
	//Função que remove a sede do usuário.
	public void removerSedeUsuarios(Sede sede) {
		//Para todos os usuário da sede, altera o papel dos mesmos para CLIENTE e remove a sua sede.
		for (Usuario cliente : sede.getClientes()) {
			if(!cliente.getPapel().equals(Papel.CLIENTE) || !cliente.getPapel().equals(Papel.MASTER))
				cliente.setPapel(Papel.CLIENTE);
			cliente.setSede(null);
			usuarioService.atualizar(cliente);
		}
	}

}
