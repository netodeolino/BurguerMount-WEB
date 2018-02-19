package com.hamburgueria.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.hamburgueria.model.Produto;
import com.hamburgueria.model.Sede;
import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.service.IngredienteService;
import com.hamburgueria.service.ProdutoService;
import com.hamburgueria.service.SedeService;
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
	
	@Autowired
	SedeService sedeService;
	
	@Autowired
	TipoIngredienteController tipoIngredienteController;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroIngrediente" um ingrediente vazio
	 *e uma lista de tipos ingredientes da sede do usuário logado.
	 * */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarIngrediente() {
		List<TipoIngrediente> tipoIngredientes = tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId());
		if (tipoIngredientes.size() >= 1) {
			ModelAndView model = new ModelAndView("ingrediente/formCadastroIngrediente");
			model.addObject(new Ingrediente());
			model.addObject("tipos", tipoIngredientes);
			return model;
		}
		ModelAndView model = new ModelAndView("tipoIngrediente/listarTipoIngredientes");
		model.addObject("tipoIngredientes", tipoIngredientes);
		model.addObject("mensagemSemTipo", "Por favor, cadastre um Tipo de Ingrediente previamente!");
		return model;
	}
	
	/*Função de cadastro, chamada quando o usuário passa o tipo de ingrediente do ingrediente a ser cadastrado.
	 *Manda para a página "formCadastroIngrediente" um ingrediente já com o tipo ingrediente informado
	 *e uma lista de tipos ingredientes da sede do usuário logado.
	 * */
	@GetMapping(path="/{id_tipo}/cadastrar")
	public ModelAndView cadastrarIngredienteComTipo(@PathVariable("id_tipo") Long id_tipo, HttpServletRequest request) {
		TipoIngrediente tipo = tipoIngredienteService.buscar(id_tipo, usuarioService.usuarioLogado().getSede().getId());
		//Caso o tipo ingrediente informado não exista, o usuário é redirecionado para uma página de erro.
		if(tipo == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Tipo Ingrediente não encontrado");
			return model;
		}
		
		//Adicionando o tipo ingrediente informado ao ingrediente que será cadastrado.
		Ingrediente ingrediente =  new Ingrediente();
		ingrediente.setTipoIngrediente(tipo);
		
		ModelAndView model = new ModelAndView("ingrediente/formCadastroIngrediente");
		model.addObject(ingrediente);
		model.addObject("tipos", tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId()));
		return model;
	}
	
	/*Função que salva o ingrediente cadastrado.
	 *Recebe um ingrediente e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public String cadastrarIngrediente(@Valid Ingrediente ingrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem, RedirectAttributes attributes) throws IOException {
		//Verifica se a disponibilidade do ingrediente e salva o ingrediente.
		this.verificaDisponibilidade(ingrediente);
		
		Ingrediente salvo = ingredienteService.salvar(ingrediente);
		
		//Adiciona o ingrediente a lista de ingredientes do seu tipo.
		TipoIngrediente tipo = tipoIngredienteService.buscar(salvo.getTipoIngrediente().getId(), usuarioService.usuarioLogado().getSede().getId());
		this.adicionarIngredienteTipo(salvo, tipo);
		
		//Adiciona o ingrediente a lista de ingredientes da sede do usuário logado.
		salvo.setSede(usuarioService.usuarioLogado().getSede());
		this.adicionarIngredienteSede(ingrediente, usuarioService.usuarioLogado().getSede());
		
		//Verifica se foi informada uma imagem, caso não: o ingrediente é salvo com uma imagem padrão.
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_INGREDIENTE);
		}
		ingredienteService.salvar(salvo);
		
		attributes.addFlashAttribute("mensagemCadastro", "Ingrediente cadastrado com Sucesso!");
		return "redirect:/ingrediente/" + tipo.getId() + "/listar";
	}
	
	/*Função que lista todos ingredientes do banco (todos da sede do usuário logado).
	 *Retorna pra página "listarIngredientes" esses ingredientes divididos em disponíveis (qtd > 0) e indisponiveis (qtd == 0).
	 */
	@GetMapping(path="/listar")
	public ModelAndView listarIngredientes() {
		ModelAndView model = new ModelAndView("ingrediente/listarIngredientes");
		List<Ingrediente> disponiveis = ingredienteService.listarDisponiveis(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("disponiveis", disponiveis);
		List<Ingrediente> indisponiveis = ingredienteService.listarIndisponiveis(usuarioService.usuarioLogado().getSede().getId());
		model.addObject("indisponiveis", indisponiveis);
		return model;
	}
	
	/*Função que lista todos ingredientes de um determinado tipo (todos da sede do usuário logado).
	 *Retorna pra página "listarIngredientes" esses ingredientes divididos em disponíveis (qtd > 0) e indisponiveis (qtd == 0).
	 */
	@GetMapping(path="/{id_tipo}/listar")
	public ModelAndView listarIngredientesPorTipo(@PathVariable("id_tipo") Long id_tipo) {
		TipoIngrediente tipo = tipoIngredienteService.buscar(id_tipo, usuarioService.usuarioLogado().getSede().getId());
		//Verifica se o tipo de ingrediente informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(tipo == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Tipo Ingrediente não encontrado");
			return model;
		}
		ModelAndView model = new ModelAndView("ingrediente/listarIngredientes");
		model.addObject("tipo", tipo);
		model.addObject("disponiveis", this.filtraDisponiveis(tipo.getIngredientes()));
		model.addObject("indisponiveis", this.filtraIndisponiveis(tipo.getIngredientes()));
		return model;	
	}
	
	//Função que exclui um determinado ingrediente.
	@GetMapping(path="/excluir/{id}")
	public String excluirIngrediente(@PathVariable("id") Long id, RedirectAttributes attributes) {
		Ingrediente ingrediente = ingredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		if(ingrediente == null) {
			return "redirect:/ingrediente/listar";
		}
		//Remove o ingrediente que será excluido da lista de ingredientes do seu tipo, e atualiza o tipo ingrediente.
		this.removerIngredienteTipo(ingrediente, ingrediente.getTipoIngrediente());
		//Remove o ingrediente que será excluido da lista de ingredientes da sua sede, e atualiza  a sede.
		this.removerIngredienteSede(ingrediente, ingrediente.getSede());
		//Remove os produtos que possuem o ingrediente que será excluído.
		this.removerProdutosIngrediente(ingrediente);
		//Adiciona a Sede do ingrediente como null para não da erro de restrição de chave e salva. 
		ingrediente.setSede(null);
		ingredienteService.salvar(ingrediente);

		ingredienteService.excluir(id);

		attributes.addFlashAttribute("mensagemExcluir", "Ingrediente excluído com Sucesso!");
		return "redirect:/ingrediente/listar";
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarIngrediente" um ingrediente informado pela URL.
	 *e uma lista de tipos ingredientes da sede do usuário logado.
	 * */
	@GetMapping(path="/editar/{id}")
	public ModelAndView editarIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		//Verifica se o ingrediente informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(ingrediente == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Ingrediente não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("ingrediente/formEditarIngrediente");
		model.addObject("ingrediente", ingrediente);
		model.addObject("tipos", tipoIngredienteService.listar(usuarioService.usuarioLogado().getSede().getId()));
		return model;
	}
	
	/*Função que salva o ingrediente modificado.
	 *Recebe um ingrediente e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarIngrediente(@Valid Ingrediente ingrediente, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		//Verifica se a disponibilidade do ingrediente.
		this.verificaDisponibilidade(ingrediente);
		
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if(imagem != null && !imagem.isEmpty()) {
			ingrediente.setFoto64(Image.imagemBase64(imagem));
		}

		//Verifica se o tipo ingrediente foi alterado e faz a mudança para o novo tipo ingrediente informado.
		Ingrediente antigo = ingredienteService.buscar(ingrediente.getId(), usuarioService.usuarioLogado().getSede().getId());
		if(!antigo.getTipoIngrediente().equals(ingrediente.getTipoIngrediente())) {
			this.removerIngredienteTipo(antigo, antigo.getTipoIngrediente());
			
			TipoIngrediente tipo = tipoIngredienteService.buscar(ingrediente.getTipoIngrediente().getId(), usuarioService.usuarioLogado().getSede().getId());
			this.adicionarIngredienteTipo(ingrediente, tipo);
		}
		ingredienteService.salvar(ingrediente);

		return "redirect:/ingrediente/listar";
	}
	
	//Função que retorna para a página "detalhesIngrediente" um ingrediente passado pela URL.
	@GetMapping(path="/{id}")
	public ModelAndView detalhesIngrediente(@PathVariable("id") Long id) {
		Ingrediente ingrediente = ingredienteService.buscar(id, usuarioService.usuarioLogado().getSede().getId());
		//Verifica se o ingrediente informado existe, caso não exista o usuário é redirecionado para uma página de erro.
		if(ingrediente == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Tipo Ingrediente não encontrado");
			return model;
		}
		
		ModelAndView model = new ModelAndView("ingrediente/detalhesIngrediente");
		model.addObject("ingrediente", ingrediente);
		return model;
	}
	
	//Adiciona o ingrediente na lista de ingredientes do tipo ingrediente e salva o tipo ingrediente.
	public void adicionarIngredienteTipo(Ingrediente ingrediente, TipoIngrediente tipo) {
		List<Ingrediente> ingredientes = tipo.getIngredientes();
		ingredientes.add(ingrediente);
		
		tipo.setIngredientes(ingredientes);
		tipoIngredienteService.salvar(tipo);
	}
	
	//Remove o ingrediente da lista de ingredientes do tipo ingrediente e salva o tipo ingrediente.
	public void removerIngredienteTipo(Ingrediente ingrediente, TipoIngrediente tipo) {
		List<Ingrediente> ingredientes = tipo.getIngredientes();
		ingredientes.remove(ingrediente);
		
		tipo.setIngredientes(ingredientes);
		tipoIngredienteService.salvar(tipo);
	}
	
	//Adiciona o ingrediente na lista de ingredientes da sede e salva a sede.
	public void adicionarIngredienteSede(Ingrediente ingrediente, Sede sede) {
		List<Ingrediente> ingredientes = sede.getEstoque();
		ingredientes.add(ingrediente);
		
		sede.setEstoque(ingredientes);
		sedeService.salvar(sede);
	}
	
	//Remove o ingrediente da lista de ingredientes da sede e salva a sede.
	public void removerIngredienteSede(Ingrediente ingrediente, Sede sede) {
		List<Ingrediente> ingredientes = sede.getEstoque();
		ingredientes.remove(ingrediente);
		
		sede.setEstoque(ingredientes);
		sedeService.salvar(sede);
	}
	
	//Remove o produto da lista de produtos da sede e salva a sede
	public void removerProdutoSede(Produto produto, Sede sede) {
		List<Produto> produtos = sede.getProdutos();
		produtos.remove(produto);
		
		sede.setProdutos(produtos);
		sedeService.salvar(sede);
	}
	
	public void removerProdutosIngrediente(Ingrediente ingrediente) {
		List<Produto> produtos = ingrediente.getProdutos();
		for (Produto produto : produtos) {
			produto.setIngredientes(null);
			produtoService.excluir(produto.getId());
		}
		ingrediente.setProdutos(null);
		ingredienteService.salvar(ingrediente);
	}
	
	//Filtra e retorna apenas os ingredientes disponíveis
	public List<Ingrediente> filtraDisponiveis(List<Ingrediente> ingredientes) {
		List<Ingrediente> disponiveis = new ArrayList<>();
		for (Ingrediente ingrediente : ingredientes) {
			if(ingrediente.isDisponivel())
				disponiveis.add(ingrediente);
		}
		return disponiveis;
	}
	
	//Filtra e retorna apenas os ingredientes indisponíveis
	public List<Ingrediente> filtraIndisponiveis(List<Ingrediente> ingredientes) {
		List<Ingrediente> indisponiveis = new ArrayList<>();
		for (Ingrediente ingrediente : ingredientes) {
			if(!ingrediente.isDisponivel())
				indisponiveis.add(ingrediente);
		}
		return indisponiveis;
	}
	
	//Verifica a disponibilidade do ingrediente e de cada produto que possui o mesmo.
	public void verificaDisponibilidade(Ingrediente ingrediente) {
		
		if(ingrediente.getQtd() == 0) {
			ingrediente.setDisponivel(false);
		}else {
			ingrediente.setDisponivel(true);
		}
		
		ingredienteService.salvar(ingrediente);
		
		List<Produto> produtos = ingrediente.getProdutos();
		if(produtos != null) {
			for (Produto produto : produtos) {
				List<Ingrediente> ingredientes = produto.getIngredientes();
				if(ingredientes != null) {
					for (Ingrediente i : ingredientes) {
						if(!i.isDisponivel() || 
								produtoService.contaIngrediente(produto.getId(), i.getId()) > i.getQtd()) {
							
							produto.setDisponivel(false);
							break;
						}else {
							produto.setDisponivel(true);
						}
					}
					produtoService.salvar(produto);
				}
			}
		}
	}

}
