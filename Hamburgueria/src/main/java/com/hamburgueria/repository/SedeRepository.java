package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Sede;

@Repository
@Transactional
public interface SedeRepository extends JpaRepository<Sede, Long> {

	public Sede findByCidade(String cidade); 
}
