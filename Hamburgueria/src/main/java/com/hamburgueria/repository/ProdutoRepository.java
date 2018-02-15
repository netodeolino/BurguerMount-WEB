package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Produto;

@Repository
@Transactional
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	//Lista todos produtos de uma determinada sede
		@Query(value = "SELECT * FROM PRODUTO "
				+ "WHERE sede_id = ?1 "
				+ "ORDER BY nome", nativeQuery=true)
		public List<Produto> listarTodos(Long id_sede);
		
		//Lista todos produtos disponiveis de uma determinada sede
		@Query(value = "SELECT * FROM PRODUTO "
				+ "WHERE sede_id = ?1 AND disponivel = true "
				+ "ORDER BY nome", nativeQuery=true)
		public List<Produto> listarDisponiveis(Long id_sede);
		
		//Lista todos produtos indisponiveis de uma determinada sede
		@Query(value = "SELECT * FROM PRODUTO "
				+ "WHERE sede_id = ?1 AND disponivel = false "
				+ "ORDER BY nome", nativeQuery=true)
		public List<Produto> listarIndisponiveis(Long id_sede);
		
		//Busca um determinado produto de uma determinada sede
		@Query(value = "SELECT * FROM PRODUTO "
				+ "WHERE id = ?1 AND sede_id = ?2", nativeQuery=true)
		public Produto buscar(Long id_produto, Long id_sede);
	
		//Conta a quantidade de um determinado ingrediente em um produto.
		@Query(value = "SELECT COUNT(*) FROM PRODUTO_INGREDIENTES "
				+ "WHERE produto_id = ?1 AND ingredientes_id = ?2", nativeQuery=true)
		public Integer contaIngrediente(Long id_produto, Long id_ingrediente);
	
}
