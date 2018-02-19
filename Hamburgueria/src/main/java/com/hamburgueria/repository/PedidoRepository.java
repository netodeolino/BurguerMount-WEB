package com.hamburgueria.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hamburgueria.model.Pedido;

@Repository
@Transactional
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	//Busca um determinado pedido de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
		+ "WHERE id = ?1 AND sede_id = ?2", nativeQuery=true)
	public Pedido buscar(Long id_pedido, Long id_sede);
	
	//Retorna a lista de todos os pedidos que possuem um determinado produto.
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE id IN ("
			+ " SELECT pedido_id FROM PEDIDO_PRODUTOS WHERE produtos_id = ?1) ", nativeQuery=true)
	public List<Pedido> buscarPedidosProduto(Long id_produto);
	
	//Lista todos pedidos de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE sede_id = ?1 "
			+ "ORDER BY data", nativeQuery=true)
	public List<Pedido> listarTodos(Long id_sede);
	
	//Lista todos pedidos em aberto de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'EM_ABERTO' AND sede_id = ?1 "
			+ "ORDER BY data", nativeQuery=true)
	public List<Pedido> listarEmAberto(Long id_sede);
	
	//Lista todos pedidos em andamento de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'EM_ANDAMENTO' AND sede_id = ?1 "
			+ "ORDER BY data", nativeQuery=true)
	public List<Pedido> listarEmAndamento(Long id_sede);
	
	//Lista todos pedidos prontos de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'PRONTO' AND sede_id = ?1 "
			+ "ORDER BY data", nativeQuery=true)
	public List<Pedido> listarProntos(Long id_sede);
	
	//Lista todos pedidos entregues de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'ENTREGUE' AND sede_id = ?1 "
			+ "ORDER BY data", nativeQuery=true)
	public List<Pedido> listarEntregues(Long id_sede);
	
	//Conta a quantidade de um determinado ingrediente de um pedido.
	@Query(value = "SELECT COUNT (ingredientes_id) FROM PRODUTO_INGREDIENTES "
			+ "LEFT OUTER JOIN PEDIDO_PRODUTOS ON produto_id = produtos_id "
			+ "WHERE ingredientes_id = ?2 AND pedido_id = ?1", nativeQuery=true)
	public Integer contaIngredientes(Long id_pedido, Long id_ingrediente);
}
