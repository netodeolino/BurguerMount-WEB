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
	
	public Ingrediente salvar(Ingrediente ingrediente) {
		return ingredienteRepository.save(ingrediente);
	}
	
	public Ingrediente buscar(Long id_ingrediente, Long id_sede) {
		return ingredienteRepository.buscar(id_ingrediente, id_sede);
	}
	
	public void excluir(Long id) {
		ingredienteRepository.delete(id);
	}
	
	public List<Ingrediente> listarTodos(Long id_sede){
		return ingredienteRepository.listarTodos(id_sede);
	}
	
	public List<Ingrediente> listarDisponiveis(Long id_sede){
		return ingredienteRepository.listarDisponiveis(id_sede);
	}
	
	public List<Ingrediente> listarIndisponiveis(Long id_sede){
		return ingredienteRepository.listarIndisponiveis(id_sede);
	}
}
