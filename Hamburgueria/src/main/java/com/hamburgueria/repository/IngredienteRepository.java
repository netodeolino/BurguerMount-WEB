package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Ingrediente;

@Repository
@Transactional
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

}
