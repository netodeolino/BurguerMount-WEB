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
	
	@Query(value = "SELECT * FROM TIPO_INGREDIENTE "
			+ "WHERE sede_id = ?1", nativeQuery=true)
	public List<TipoIngrediente> listar(Long id_sede);
	
}
