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
	
	public Ingrediente buscar(Long id) {
		return ingredienteRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		ingredienteRepository.delete(id);
	}
	
	public List<Ingrediente> listar(){
		return ingredienteRepository.findAll();
	}
}
