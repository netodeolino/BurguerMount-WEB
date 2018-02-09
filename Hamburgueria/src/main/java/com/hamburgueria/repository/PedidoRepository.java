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
	
	//Lista todos pedidos de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE sede_id = ?1", nativeQuery=true)
	public List<Pedido> listarTodos(Long id_sede);
	
	//Lista todos pedidos em aberto de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'EM_ABERTO' AND sede_id = ?1", nativeQuery=true)
	public List<Pedido> listarEmAberto(Long id_sede);
	
	//Lista todos pedidos em andamento de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'EM_ANDAMENTO' AND sede_id = ?1", nativeQuery=true)
	public List<Pedido> listarEmAndamento(Long id_sede);
	
	//Lista todos pedidos prontos de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'PRONTO' AND sede_id = ?1", nativeQuery=true)
	public List<Pedido> listarProntos(Long id_sede);
	
	//Lista todos pedidos entregues de uma determinada sede
	@Query(value = "SELECT * FROM PEDIDO "
			+ "WHERE status = 'ENTREGUE' AND sede_id = ?1", nativeQuery=true)
	public List<Pedido> listarEntregues(Long id_sede);
}
