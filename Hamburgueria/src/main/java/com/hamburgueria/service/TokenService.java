package com.hamburgueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Token;
import com.hamburgueria.model.Usuario;
import com.hamburgueria.repository.TokenRepository;

@Service
@ComponentScan("com.hamburgueria.service")
public class TokenService {

	@Autowired
	private TokenRepository tokenRepository;
	

	public Token buscarPorUsuario(Usuario usuario) {
		return tokenRepository.findByUsuario(usuario);
	}

	public Token buscar(String token) {
		return tokenRepository.findOne(token);
	}

	public boolean existe(String token) {
		return tokenRepository.exists(token);
	}

	public void salvar(Token token) {
		tokenRepository.save(token);
	}

	public void deletar(Token token) {
		tokenRepository.delete(token);
	}
}
