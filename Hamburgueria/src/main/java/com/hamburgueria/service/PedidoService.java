package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Pedido;
import com.hamburgueria.repository.PedidoRepository;

@Service
public class PedidoService {

	@Autowired
	PedidoRepository pedidoRepository;
	
	public Pedido salvar(Pedido pedido) {
		return pedidoRepository.save(pedido);
	}
	
	public Pedido buscar(Long id_pedido, Long id_sede) {
		return pedidoRepository.buscar(id_pedido, id_sede);
	}
	
	public void excluir(Long id) {
		pedidoRepository.delete(id);
	}
	
	public List<Pedido> listarTodos(Long id_sede){
		return pedidoRepository.listarTodos(id_sede);
	}
	
	public List<Pedido> listarEmAberto(Long id_sede){
		return pedidoRepository.listarEmAberto(id_sede);
	}
	
	public List<Pedido> listarEmAndamento(Long id_sede){
		return pedidoRepository.listarEmAndamento(id_sede);
	}
	
	public List<Pedido> listarProntos(Long id_sede){
		return pedidoRepository.listarProntos(id_sede);
	}
	
	public List<Pedido> listarEntregues(Long id_sede){
		return pedidoRepository.listarEntregues(id_sede);
	}
	
	//Retorna a lista de todos os pedidos que possuem um determinado produto.
	public List<Pedido> buscarPedidosProduto(Long id_produto){
		return pedidoRepository.buscarPedidosProduto(id_produto);
	}
	
	public Integer contaIngredientes(Long id_pedido, Long id_ingrediente) {
		return pedidoRepository.contaIngredientes(id_pedido, id_ingrediente);
	}
}
