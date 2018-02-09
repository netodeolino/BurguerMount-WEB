package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	//Busca um usuário por email.
	public Usuario findByEmail(String email);
	
	//Lista todos usuarios.
	@Query(value = "SELECT * FROM USUARIO "
			+ "ORDER BY papel", nativeQuery=true)
	public List<Usuario> listarTodos();
	
	//Lista todos usuarios de uma determinada sede.
	@Query(value = "SELECT * FROM USUARIO "
			+ "WHERE sede_id = ?1 and papel != 'MASTER' "
			+ "ORDER BY papel", nativeQuery=true)
	public List<Usuario> listar(Long id_sede);
	
	//Calcula o total gasto por um determinado usuário.
	@Query(value = "SELECT SUM(preco) FROM PEDIDO "
			+ "WHERE cliente_id = ?1", nativeQuery=true)
	public Double calcularConsumo(Long usuario_id);
}
