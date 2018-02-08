package com.hamburgueria.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Token;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	private TokenService tokenService;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UsuarioService() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}
	
	//Função que salva um usuário, antes de salvar ele criptografa a senha.
	public Usuario salvar(Usuario usuario) {
		usuario.setSenha(bCryptPasswordEncoder.encode(usuario.getSenha()));
		return usuarioRepository.save(usuario);
	}
	
	//Função que salva um usuário.
	public Usuario atualizar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	//Busca um usuário pelo ID.
	public Usuario buscar(Long id) {
		return usuarioRepository.findOne(id);
	}
	
	//Busca um usuário pelo email.
	public Usuario buscar(String email) {
		return usuarioRepository.findByEmail(email);
	}
	
	//Exclui um usuário pelo ID.
	public void excluir(Long id) {
		usuarioRepository.delete(id);
	}
	
	//Lista todos usuários.
	public List<Usuario> listarTodos() {
		return usuarioRepository.listarTodos();
	}
	
	//Lista todos usuários de uma sede.
	public List<Usuario> listar(Long sede_id) {
		return usuarioRepository.listar(sede_id);
	}
	
	//Calcula o total gasto por um determinado usuário.
	public Double calcularConsumo(Long usuario_id) {
		return usuarioRepository.calcularConsumo(usuario_id);
	}
	
	//Função que valida o login de um usuário através do seu email e senha informados.
	public boolean logar(String email, String senha){
		Usuario userBanco = usuarioRepository.findByEmail(email);
		/*Verifica se o usuário com esse email existe e se a senha passada é igual a senha no banco.
		*Antes ele criptografa a senha informada para comparar com a senha que está no banco.
		*/
		if(userBanco != null && new BCryptPasswordEncoder().matches(senha, userBanco.getSenha())) return true;
		else return false;
	}
	
	/*Retorna o usuário logado.
	 *Pega o email do usuário logado através do Authentication e busca esse usuário no banco.
	 */
	public Usuario usuarioLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		Usuario usuarioLogado = this.buscar(email);
		return usuarioLogado;
	}
	
	//Recebe uma senha e uma senha criptografada e compara as duas.
	public boolean compararSenha(String senhaLimpa, String senhaCriptografada) {
		if (new BCryptPasswordEncoder().matches(senhaLimpa, senhaCriptografada)) {
			return true;
		}
		return false;
	}
	
	public void recuperarSenha(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email);
		
		if (usuario != null) {
			Token token = null;
			token = tokenService.buscarPorUsuario(usuario);
			
			if (token == null) {
				token = new Token();
				token.setUsuario(usuario);
				token.setToken(UUID.randomUUID().toString());
				tokenService.salvar(token);
			}
		}
	}
	
	public void novaSenha(String tokenStr, String senha) {
		Token token = tokenService.buscar(tokenStr);
		if (token != null && !token.expirou()) {
			Usuario usuario = token.getUsuario();
			usuario.setSenha(senha);
			salvar(usuario);
			tokenService.deletar(token);
		}
		
		if(token != null && token.expirou()) tokenService.deletar(token);
	}
}
