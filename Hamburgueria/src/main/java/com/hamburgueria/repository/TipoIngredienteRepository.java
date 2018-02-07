package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.TipoIngrediente;

@Repository
@Transactional
public interface TipoIngredienteRepository extends JpaRepository<TipoIngrediente, Long> {
	
	//Lista todos tipo ingredientes de uma determinada sede
	@Query(value = "SELECT * FROM TIPO_INGREDIENTE "
			+ "WHERE sede_id = ?1 "
			+ "ORDER BY nome", nativeQuery=true)
	public List<TipoIngrediente> listar(Long id_sede);
	
	//Busca um determinado tipo ingrediente de uma determinada sede
	@Query(value = "SELECT * FROM TIPO_INGREDIENTE "
			+ "WHERE id = ?1 AND sede_id = ?2", nativeQuery=true)
	public TipoIngrediente buscar(Long id_tipo, Long id_sede);
	
}
