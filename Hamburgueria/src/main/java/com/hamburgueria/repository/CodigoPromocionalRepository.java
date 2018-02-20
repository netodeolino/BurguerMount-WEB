package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.CodigoPromocional;

@Repository
@Transactional
public interface CodigoPromocionalRepository extends JpaRepository<CodigoPromocional, Long>{

	//Busca um codigo pela string do seu codigo
	public CodigoPromocional findByCodigo(String codigo); 
	
}
