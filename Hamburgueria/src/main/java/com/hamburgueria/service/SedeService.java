package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Sede;
import com.hamburgueria.repository.SedeRepository;

@Service
public class SedeService {

	@Autowired
	SedeRepository sedeRepository;
	
	//Salva uma determinada sede
	public Sede salvar(Sede sede) {
		return sedeRepository.save(sede);
	}
	
	//Busca uma determinada sede
	public Sede buscar(Long id) {
		return sedeRepository.findOne(id);
	}
	
	//Busca uma determinada sede pela cidade
	public Sede buscar(String cidade) {
		return sedeRepository.findByCidade(cidade);
	}
	
	//Exclui uma determinada sede
	public void excluir(Long id) {
		sedeRepository.delete(id);
	}
	
	//Lista todas as sedes do banco
	public List<Sede> listar(){
		return sedeRepository.findAll();
	}
}
