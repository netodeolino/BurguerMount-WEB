package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.TipoIngrediente;

@Repository
@Transactional
public interface TipoIngredienteRepository extends JpaRepository<TipoIngrediente, Long> {

}
