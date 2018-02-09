package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Ingrediente;

@Repository
@Transactional
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
	
	//Lista todos ingredientes de uma determinada sede
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1 "
			+ "ORDER BY nome", nativeQuery=true)
	public List<Ingrediente> listarTodos(Long id_sede);
	
	//Lista todos ingredientes disponiveis (qtd > 0) de uma determinada sede
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1 AND disponivel = true "
			+ "ORDER BY nome", nativeQuery=true)
	public List<Ingrediente> listarDisponiveis(Long id_sede);
	
	//Lista todos ingredientes indisponiveis (qtd == 0) de uma determinada sede
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1 AND disponivel = false "
			+ "ORDER BY nome", nativeQuery=true)
	public List<Ingrediente> listarIndisponiveis(Long id_sede);
	
	//Busca um determinado ingrediente de uma determinada sede
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE id = ?1 AND sede_id = ?2", nativeQuery=true)
	public Ingrediente buscar(Long id_tipo, Long id_sede);
	
}
