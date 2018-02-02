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
	
	//Salva um tipo ingrediente
	public TipoIngrediente salvar(TipoIngrediente tipoIngrediente) {
		return tipoIngredienteRepository.save(tipoIngrediente);
	}
	
	//Busca um determinado tipo ingrediente de uma determinada sede
	public TipoIngrediente buscar(Long id_tipo, Long id_sede) {
		return tipoIngredienteRepository.buscar(id_tipo, id_sede);
	}
	
	//Deleta um determinado tipo ingrediente
	public void excluir(Long id) {
		tipoIngredienteRepository.delete(id);
	}
	
	//Lista todos tipo ingredientes de uma determinada sede
	public List<TipoIngrediente> listar(Long sede_id){
		return tipoIngredienteRepository.listar(sede_id);
	}
	
}
