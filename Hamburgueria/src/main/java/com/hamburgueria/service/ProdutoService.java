package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Produto;
import com.hamburgueria.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	ProdutoRepository produtoRepository;
	
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}
	
	public Produto buscar(Long id) {
		return produtoRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		produtoRepository.delete(id);
	}
	
	public List<Produto> listar(){
		return produtoRepository.findAll();
	}
}
