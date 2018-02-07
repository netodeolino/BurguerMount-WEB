package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Produto;
import com.hamburgueria.model.Sede;

@Repository
@Transactional
public interface SedeRepository extends JpaRepository<Sede, Long> {

	//Busca uma sede por cidade
	public Sede findByCidade(String cidade); 
	
}
