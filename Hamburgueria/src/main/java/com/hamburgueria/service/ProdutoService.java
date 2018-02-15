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
	
	//Salva um determinado produto.
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}
	
	//Busca um determinado produto de uma determinada sede.
	public Produto buscar(Long id_produto, Long id_sede) {
		return produtoRepository.buscar(id_produto, id_sede);
	}
	
	//Exclui um determinado produto.
	public void excluir(Long id) {
		produtoRepository.delete(id);
	}
	
	//Lista todos produtos de uma determinada sede
	public List<Produto> listarTodos(Long id_sede){
		return produtoRepository.listarTodos(id_sede);
	}
	
	//Lista todos produtos disponiveis de uma determinada sede
	public List<Produto> listarDisponiveis(Long id_sede){
		return produtoRepository.listarDisponiveis(id_sede);
	}
		
	//Lista todos produtos indisponiveis de uma determinada sede
	public List<Produto> listarIndisponiveis(Long id_sede){
		return produtoRepository.listarIndisponiveis(id_sede);
	}
	
	//Conta a quantidade de um determinado ingrediente em um produto
	public Integer contaIngrediente(Long id_produto, Long id_ingrediente) {
		return produtoRepository.contaIngrediente(id_produto, id_ingrediente);
	}

}
