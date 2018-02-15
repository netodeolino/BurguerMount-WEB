package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Ingrediente;
import com.hamburgueria.repository.IngredienteRepository;

@Service
public class IngredienteService {

	@Autowired
	IngredienteRepository ingredienteRepository;
	
	//Salva um ingrediente
	public Ingrediente salvar(Ingrediente ingrediente) {
		return ingredienteRepository.save(ingrediente);
	}
	
	//Busca um determinado ingrediente de uma determinada sede
	public Ingrediente buscar(Long id_ingrediente, Long id_sede) {
		return ingredienteRepository.buscar(id_ingrediente, id_sede);
	}
	
	//Deleta um determinado ingrediente
	public void excluir(Long id) {
		ingredienteRepository.delete(id);
	}
	
	//Lista todos ingredientes de uma determinada sede
	public List<Ingrediente> listarTodos(Long id_sede){
		return ingredienteRepository.listarTodos(id_sede);
	}
	
	//Lista todos ingredientes disponiveis (qtd > 0) de uma determinada sede
	public List<Ingrediente> listarDisponiveis(Long id_sede){
		return ingredienteRepository.listarDisponiveis(id_sede);
	}
	
	//Lista todos ingredientes indisponiveis (qtd == 0) de uma determinada sede
	public List<Ingrediente> listarIndisponiveis(Long id_sede){
		return ingredienteRepository.listarIndisponiveis(id_sede);
	}
	
	//Lista de ingredientes de um pedido.
	public List<Ingrediente> getIngredientes(Long id_pedido){
		return ingredienteRepository.getIngredientes(id_pedido);
	}
}
