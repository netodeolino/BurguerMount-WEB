package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.model.TipoIngrediente;

@Repository
@Transactional
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
	
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1", nativeQuery=true)
	public List<Ingrediente> listarTodos(Long id_sede);
	
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1 AND disponivel = true", nativeQuery=true)
	public List<Ingrediente> listarDisponiveis(Long id_sede);
	
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE sede_id = ?1 AND disponivel = false", nativeQuery=true)
	public List<Ingrediente> listarIndisponiveis(Long id_sede);
	
	@Query(value = "SELECT * FROM INGREDIENTE "
			+ "WHERE id = ?1 AND sede_id = ?2", nativeQuery=true)
	public Ingrediente buscar(Long id_tipo, Long id_sede);
	
}
