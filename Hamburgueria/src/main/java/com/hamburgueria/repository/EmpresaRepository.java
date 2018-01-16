package com.hamburgueria.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Empresa;

@Repository
@Transactional
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

}
