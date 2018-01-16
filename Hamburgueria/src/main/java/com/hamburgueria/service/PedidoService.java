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
	
	public Pedido buscar(Long id) {
		return pedidoRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		pedidoRepository.delete(id);
	}
	
	public List<Pedido> listar(){
		return pedidoRepository.findAll();
	}
}
