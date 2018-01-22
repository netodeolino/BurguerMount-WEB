package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.TipoIngrediente;
import com.hamburgueria.repository.TipoIngredienteRepository;

@Service
public class TipoIngredienteService {

	@Autowired
	TipoIngredienteRepository tipoIngredienteRepository;
	
	public TipoIngrediente salvar(TipoIngrediente tipoIngrediente) {
		return tipoIngredienteRepository.save(tipoIngrediente);
	}
	
	public TipoIngrediente buscar(Long id) {
		return tipoIngredienteRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		tipoIngredienteRepository.delete(id);
	}
	
	public List<TipoIngrediente> listar(){
		return tipoIngredienteRepository.findAll();
	}
	
}
