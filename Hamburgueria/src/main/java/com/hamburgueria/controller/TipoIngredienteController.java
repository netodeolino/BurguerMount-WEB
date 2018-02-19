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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.service.IngredienteService;
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
	
	@Autowired
	IngredienteService ingredienteService;
	
	@Autowired
	IngredienteController ingredienteController;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroTipoIngrediente" um tipo ingrediente vazio
	 * */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarTipoIngrediente(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("tipoIngrediente/formCadastroTipoIngrediente");
		model.addObject(new TipoIngrediente());
		return model;
	}
	
	/*Função que salva o tipo ingrediente cadastrado.
	 *Recebe um tipo ingrediente e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public String cadastrarTipoIngrediente(@Valid TipoIngrediente tipoIngrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem, RedirectAttributes attributes) throws IOException {
		
		TipoIngrediente salvo = tipoIngredienteService.salvar(tipoIngrediente);
		
		//Verifica se a quantidade do ingrediente e coloca a disponibilidade do mesmo.
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_TIPO_INGREDIENTE);
		}
		
		//Adiciona o tipo ingrediente a lista de tipo ingredientes da sede do usuário logado.
		salvo.setSede(usuarioService.usuarioLogado().getSede());
		this.adicionarTipoIngredienteSede(tipoIngrediente, usuarioService.usuarioLogado().getSede());
		
		tipoIngredienteService.salvar(salvo);
		
		attributes.addFlashAttribute("mensagemCadastro", "Cadastro realizado com Sucesso!");
		return "redirect:/tipo_ingrediente/listar";
	}
	
	//Função que lista todos tipo ingredientes do banco (todos da sede do usuário logado).
	@GetMapping(path="/listar")
	public ModelAndView listarTipoIngredientes(){
		ModelAndView model = new ModelAndView("tipoIngrediente/listarTipoIngredientes");
		List<TipoIngrediente> tipoIngredientes = tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("tipoIngredientes", tipoIngredientes);
		return model;
	}
	
	//Função que exclui um determinado tipo ingrediente.
	@GetMapping(path="/excluir/{id}")
	public String excluirTipoIngrediente(@PathVariable("id") Long id, RedirectAttributes attributes) {
		TipoIngrediente tipoIngrediente = tipoIngredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if (tipoIngrediente == null) {
			return "redirect:/tipo_ingrediente/listar";
		}
		
		//Remove o tipo ingrediente que será excluido da lista de tipo ingredientes da sua sede, e atualiza  a sede.
		this.removerTipoIngredienteSede(tipoIngrediente, tipoIngrediente.getSede());
		
		//Remove todos ingredientes do tipo ingrediente que será excluido 
		//da lista de ingredientes da sua sede, e atualiza  a sede.
		this.excluirIngredientesTipoIngrediente(tipoIngrediente);
		tipoIngredienteService.excluir(tipoIngrediente.getId());
		
		attributes.addFlashAttribute("mensagemExcluir", "Tipo de Ingrediente excluído com Sucesso!");
		return "redirect:/tipo_ingrediente/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarTipoIngrediente" um tipo ingrediente informado pela URL.
	 **/
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarTipoIngrediente(@PathVariable("id") Long id) {
		TipoIngrediente tipoIngrediente = tipoIngredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		//Verifica se o tipo ingrediente informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(tipoIngrediente == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Tipo Ingrediente não encontrado");
			return model;
		}			
		
		ModelAndView model = new ModelAndView("tipoIngrediente/formEditarTipoIngrediente");
		model.addObject("tipoIngrediente", tipoIngrediente);
		return model;
	}
	
	/*Função que salva o tipo ingrediente modificado.
	 *Recebe um tipo ingrediente e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarTipoIngrediente(@Valid TipoIngrediente tipoIngrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if(imagem != null && !imagem.isEmpty()) {
			tipoIngrediente.setFoto64(Image.imagemBase64(imagem));
		}
		
		tipoIngredienteService.salvar(tipoIngrediente);
		
		return "redirect:/tipo_ingrediente/listar";
	}
	
	//Adiciona o tipo ingrediente na lista de tipo ingredientes da sede e salva a sede.
	public void adicionarTipoIngredienteSede(TipoIngrediente tipoIngrediente, Sede sede) {
		List<TipoIngrediente> tipos = sede.getTipoIngredientes();
		tipos.add(tipoIngrediente);
		sede.setTipoIngredientes(tipos);
		
		sedeService.salvar(sede);
	}
	
	//Remove o tipo ingrediente da lista de tipo ingredientes da sede e salva a sede.
	public void removerTipoIngredienteSede(TipoIngrediente tipoIngrediente, Sede sede) {
		List<TipoIngrediente> tipos = sede.getTipoIngredientes();
		tipos.remove(tipoIngrediente);
		sede.setTipoIngredientes(tipos);
		
		sedeService.salvar(sede);
	}
	
	public void removerIngredientesDeTipoIngrediente(TipoIngrediente tipoIngrediente) {
		List<Ingrediente> ingredientes = tipoIngrediente.getIngredientes();
		for (Ingrediente ingrediente : ingredientes) {
			ingredienteService.excluir(ingrediente.getId());
		}
		tipoIngrediente.setIngredientes(null);
		tipoIngredienteService.salvar(tipoIngrediente);
	}
	
	//Exclui todos os produtos de um ingrediente.
	public void excluirIngredientesTipoIngrediente(TipoIngrediente tipoIngrediente) {
		List<Ingrediente> ingredientes = tipoIngrediente.getIngredientes();
		Integer tamanho = ingredientes.size();
		
		for (int i = 0; i < tamanho; i++) {
			this.excluirIngrediente(ingredientes.get(0));
		}
	}
	
	//Exclui um determinado produto.
	public void excluirIngrediente(Ingrediente ingrediente) {
		this.ingredienteController.removerIngredienteTipo(ingrediente, ingrediente.getTipoIngrediente());
		this.ingredienteController.removerIngredienteSede(ingrediente, ingrediente.getSede());
		this.ingredienteController.excluirProdutosIngrediente(ingrediente);
		ingredienteService.excluir(ingrediente.getId());
	}
}
