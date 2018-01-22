package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Token;
import com.hamburgueria.model.Usuario;

@Repository
@Transactional
public interface TokenRepository extends JpaRepository<Token, String> {

	Token findByUsuario(Usuario usuario);
}
