package com.hamburgueria.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping(path="/usuario")
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	SedeService sedeService;
	
	/*Função de cadastro simples.
	 *Manda para a página "formCadastroUsuario" um usuario vazio
	 *e uma lista de sedes cadastradas no banco.
	 * */
	@GetMapping(path="/cadastrar")
	public ModelAndView cadastrarUsuario(HttpServletRequest request) {
		List<Sede> sedes = sedeService.listar();
		
		ModelAndView model = new ModelAndView("usuario/formCadastrarUsuario");
		model.addObject(new Usuario());
		model.addObject("sedes", sedes);
		return model;
	}
	
	/*Função que salva o usuario cadastrado.
	 *Recebe um usuario e uma possível imagem.
	 */
	@PostMapping(path="/cadastrar")
	public String cadastrarUsuario(@Valid Usuario usuario, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException {
		//Coloca o papel do usuário como CLIENTE por padrão.	
		usuario.setPapel(Papel.CLIENTE);
		Usuario salvo = usuarioService.salvar(usuario);
		
		//Verifica se foi informada uma imagem, caso não: o usuário é salvo com uma imagem padrão.
		if (imagem != null && !imagem.isEmpty()) {
			salvo.setFoto64(Image.imagemBase64(imagem));
		} else {
			salvo.setFoto64(Constants.IMAGE_DEFAULT_USUARIO);
		}
		
		//Verifica se o usuário informou alguma sede.
		if(usuario.getSede() != null) {
			//Adiciona a sede informada ao usuário que será cadastrado.
			Sede sede = sedeService.buscar(usuario.getSede().getId());
			usuario.setSede(sede);
			
			//Adiciona o usuário a sede informada e salva a sede no banco.
			this.adicionarUsuarioSede(usuario, sede);
		}
		usuarioService.atualizar(salvo);
		
		return "redirect:/";
	}
	
	/*Função que mostra os detalhes do usuário logado.
	 *Envia para a página "detalhesUsuario" o usuário que está logado.
	 */
	@GetMapping(path = "/meu_perfil")
	public ModelAndView visualizarUsuario() {
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/perfilUsuario");
		model.addObject("usuario", usuario);
		return model;
	}
	
	/*Função de edição.
	 *Manda para a página "formEditarUsuario" o usuário logado.
	 *e uma lista de todas as sedes do banco.
	 * */
	@GetMapping(path = "/editar")
	public ModelAndView editarUsuario() {
		List<Sede> sedes = sedeService.listar();
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/formEditarUsuario");
		model.addObject("usuario", usuario);
		model.addObject("sedes", sedes);
		return model;
	}
	
	/*Função que salva o usuario modificado.
	 *Recebe um usuario e uma possível imagem.
	 */
	@PostMapping(path="/editar")
	public String editarUsuario(@Valid Usuario usuario, BindingResult result, @RequestParam(value="imagem", required=false) MultipartFile imagem) throws IOException { 
		//Verifica se foi informada uma imagem, e altera ela para base64.
		if(imagem != null && !imagem.isEmpty()) {
			usuario.setFoto64(Image.imagemBase64(imagem));
		}

		Usuario antigo = usuarioService.buscar(usuario.getId());

		//Verifica se a sede foi alterada e faz a mudança para a nova sede informada.
		if(antigo.getSede()!= null && !antigo.getSede().equals(usuario.getSede())) {
			this.removerUsuarioSede(antigo, antigo.getSede());
			
			this.adicionarUsuarioSede(usuario, usuario.getSede());
		}
		usuarioService.atualizar(usuario);
		
		return "redirect:/usuario/meu_perfil";
	}
	
	/*Função que permite que o usuário logado altere sua senha.
	 *Retorna o usuário logado para a página "formAlterarSenha".
	 */
	@GetMapping(path="/alterar_senha")
	public ModelAndView alterarSenha() {
		Usuario usuario = usuarioService.usuarioLogado();
		
		ModelAndView model = new ModelAndView("usuario/formAlterarSenha");
		model.addObject("usuario", usuario);
		return model;
	}
	
	/*Função que salva o usuario com a nova senha.
	 *Recebe um usuario já com a nova senha e a senha antiga para verificação.
	 */
	@PostMapping(path = "/alterar_senha")
	public String alterarSenha(Usuario usuario, @RequestParam String senhaAtual) throws IOException {
		Usuario usuarioLogado = usuarioService.usuarioLogado();
		Usuario usuarioBanco = usuarioService.buscar(usuarioLogado.getEmail());
		//Verifica se foi enviado alguma senha.
		if(senhaAtual != null && !senhaAtual.isEmpty()){
			//Compara a senha antiga (senha do banco) e a senha atual informada para validar que o usuário sabia a senha.
			if(usuarioService.compararSenha(senhaAtual, usuarioBanco.getSenha())){
				//Altera o usuário colocando a sua senha nova e atualiza no banco.
				usuarioBanco.setSenha(usuario.getSenha());
				usuarioService.salvar(usuarioBanco);
				
				//Atualiza as informações do usuario logado.
				Authentication authentication = new UsernamePasswordAuthenticationToken(usuarioBanco, usuarioBanco.getSenha(), usuarioBanco.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				return "redirect:/usuario/meu_perfil";
			}
		}
		return "redirect:/usuario/alterar_senha";
	}
	
	/*Função que envia para a página "listarUsuarios" uma lista de usuários.
	 *Se o usuário for adm, a função envia apenas os usuários da sede desse usuário.
	 *Se o usuário for master, a função retorna todos os usuários de todas as sedes.
	 */
	@GetMapping(path="/listar_usuarios")
	public ModelAndView listarUsuarios() {
		List<Usuario> usuarios =  new ArrayList<>();
		
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR))
			usuarios = usuarioService.listar(usuarioService.usuarioLogado().getSede().getId());
		
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.MASTER))
			usuarios = usuarioService.listarTodos();
		
		usuarios.remove(usuarioService.usuarioLogado());
		
		ModelAndView model = new ModelAndView("usuario/listarUsuarios");
		model.addObject("usuarios", usuarios);
		return model;
	}
	
	//Função que retorna para a página "detalhesUsuario" um usuário passado por URL.
	@GetMapping(path="/perfil_usuario/{id_usuario}")
	public ModelAndView visualizarUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		
		//Verifica se o usuário existe, caso contrário o usuário é redirecionado para uma página de erro.
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		//Se o usuário logado for ADM, ele tem permissão de visualizar o perfil só de usuários da sua sede e os não MASTER.
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR)) {
			if(!usuario.getSede().equals(usuarioService.usuarioLogado().getSede()) || usuario.getPapel().equals(Papel.MASTER)) {
				ModelAndView model = new ModelAndView("erros/erro");
				model.addObject("mensagem", "Usuário não encontrado!");
				return model;
			}
		}
		
		ModelAndView model = new ModelAndView("usuario/detalhesUsuario");
		model.addObject("usuario", usuario);
		return model;
	}
	
	@GetMapping(path="/editar_papel/{id_usuario}")
	public ModelAndView editarPapelUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		
		//Verifica se o usuário existe, caso contrário o usuário é redirecionado para uma página de erro.
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		//Se o usuário logado for ADM, ele tem permissão de alterar o papel só de usuários da sua sede e os não MASTER.
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR)) {
			if(!usuario.getSede().equals(usuarioService.usuarioLogado().getSede()) || usuario.getPapel().equals(Papel.MASTER)) {
				ModelAndView model = new ModelAndView("erros/erro");
				model.addObject("mensagem", "Usuário não encontrado!");
				return model;
			}	
		}
		
		ModelAndView model = new ModelAndView("usuario/alterarPapel");
		model.addObject("usuario", usuario);
		return model;
	}
	
	//Função que altera o papel do usuário passado no formulário e atualiza essa informação.
	@PostMapping(path="/editar_papel")
	public String editarPapelUsuario(@Valid Usuario usuario) {
		Usuario usuarioBanco = usuarioService.buscar(usuario.getId());
		
		if(usuario.getPapel().equals(usuarioBanco.getPapel()))
			return "redirect:/usuario/listar_usuarios";
		
		usuarioService.atualizar(usuario);
		return "redirect:/usuario/perfil_usuario/" + usuario.getId();
	}
	
	//Função que retorna para a página "detalhesConsumo" um usuário, o total de pedidos que ele fez e o total gasto.
	@GetMapping(path="/consumo/{id_usuario}")
	public ModelAndView consumoUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		
		//Verifica se o usuário existe, caso contrário o usuário é redirecionado para uma página de erro.
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		//Se o usuário logado for ADM, ele tem permissão de visualizar o consumo só de usuários da sua sede e os não MASTER.
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR)) {
			if(!usuario.getSede().equals(usuarioService.usuarioLogado().getSede()) || usuario.getPapel().equals(Papel.MASTER)) {
				ModelAndView model = new ModelAndView("erros/erro");
				model.addObject("mensagem", "Usuário não encontrado!");
				return model;
			}
		}
		
		ModelAndView model = new ModelAndView("usuario/detalhesConsumo");
		model.addObject("usuario", usuario);
		model.addObject("totalPedidos", usuario.getPedidos().size());
		model.addObject("totalGasto", usuarioService.calcularConsumo(usuario.getId()));
		return model;
	}
	
	//Função que retorna para a página "listaProdutos" o usuário informado por URL.
	@GetMapping(path="/listar_produtos/{id_usuario}")
	public ModelAndView produtosUsuario(@PathVariable ("id_usuario") Long id_usuario) {
		Usuario usuario = usuarioService.buscar(id_usuario);
		
		//Verifica se o usuário existe, caso contrário o usuário é redirecionado para uma página de erro.
		if(usuario == null) {
			ModelAndView model = new ModelAndView("erros/erro");
			model.addObject("mensagem", "Usuário não encontrado!");
			return model;
		}
		
		//Se o usuário logado for ADM, ele tem permissão de visualizar os produtos só de usuários da sua sede e os não MASTER.
		if(usuarioService.usuarioLogado().getPapel().equals(Papel.ADMINISTRADOR)) {
			if(!usuario.getSede().equals(usuarioService.usuarioLogado().getSede()) || usuario.getPapel().equals(Papel.MASTER)) {
				ModelAndView model = new ModelAndView("erros/erro");
				model.addObject("mensagem", "Usuário não encontrado!");
				return model;
			}
		}
		
		ModelAndView model = new ModelAndView("usuario/listaProdutos");
		model.addObject("usuario", usuario);
		return model;
	}
	
	//Remove o usuário na lista de usuário de uma sede e salva a sede.
	public void removerUsuarioSede(Usuario usuario, Sede sede) {
		List<Usuario> clientes = sede.getClientes();
		clientes.remove(usuario);
		
		sede.setClientes(clientes);
		sedeService.salvar(sede);
	}
	
	//Adiciona o usuário na lista de usuário de uma sede e salva a sede.
	public void adicionarUsuarioSede(Usuario usuario, Sede sede) {
		List<Usuario> clientes = sede.getClientes();
		clientes.add(usuario);
		
		sede.setClientes(clientes);
		sedeService.salvar(sede);
	}
	
}
