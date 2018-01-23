package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Produto;
import com.hamburgueria.model.ProdutoIngrediente;

@Repository
@Transactional
public interface ProdutoIngredienteRepository extends JpaRepository<ProdutoIngrediente, Long> {

	List<ProdutoIngrediente> findByProduto(Produto produto);
}
