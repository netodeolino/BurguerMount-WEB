package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Usuario;
import com.hamburgueria.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UsuarioService() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}
	
	public Usuario salvar(Usuario usuario) {
		usuario.setSenha(bCryptPasswordEncoder.encode(usuario.getSenha()));
		return usuarioRepository.save(usuario);
	}
	
	public Usuario buscar(Long id) {
		return usuarioRepository.findOne(id);
	}
	
	public Usuario buscar(String email) {
		return usuarioRepository.findByEmail(email);
	}
	
	public void excluir(Long id) {
		usuarioRepository.delete(id);
	}
	
	public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}
	
	public boolean logar(String email, String senha) {
		Usuario userBanco = usuarioRepository.findByEmail(email);
		if (userBanco != null && new BCryptPasswordEncoder().matches(senha, userBanco.getSenha())) {
			return true;
		}
		return false;
	}
	
	public Usuario usuarioLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		Usuario usuarioLogado = this.buscar(email);
		return usuarioLogado;
	}
	
	public boolean compararSenha(String senhaCrip, String senhaLimpa) {
		if (new BCryptPasswordEncoder().matches(senhaLimpa, senhaCrip)) {
			return true;
		}
		return false;
	}
}
