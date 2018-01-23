package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Produto;
import com.hamburgueria.model.ProdutoIngrediente;
import com.hamburgueria.repository.ProdutoIngredienteRepository;


@Service
public class ProdutoIngredienteService {

	@Autowired
	ProdutoIngredienteRepository produtoIngredienteRepository;
	
	public ProdutoIngrediente salvar(ProdutoIngrediente produtoIngrediente) {
		return produtoIngredienteRepository.save(produtoIngrediente);
	}
	
	public ProdutoIngrediente buscar(Long id) {
		return produtoIngredienteRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		produtoIngredienteRepository.delete(id);
	}
	
	public List<ProdutoIngrediente> listar(){
		return produtoIngredienteRepository.findAll();
	}
	
	public List<ProdutoIngrediente> listar(Produto produto){
		return produtoIngredienteRepository.findByProduto(produto);
	}
}
