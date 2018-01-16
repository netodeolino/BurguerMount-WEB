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
	
	public Sede salvar(Sede sede) {
		return sedeRepository.save(sede);
	}
	
	public Sede buscar(Long id) {
		return sedeRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		sedeRepository.delete(id);
	}
	
	public List<Sede> listar(){
		return sedeRepository.findAll();
	}
}
