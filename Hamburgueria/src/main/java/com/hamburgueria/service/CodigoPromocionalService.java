package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.CodigoPromocional;
import com.hamburgueria.repository.CodigoPromocionalRepository;

@Service
public class CodigoPromocionalService {

	@Autowired
	CodigoPromocionalRepository codigoRepository;
	
	//Salva um determinado codigo.
	public CodigoPromocional salvar(CodigoPromocional codigo) {
		return codigoRepository.save(codigo);
	}
	
	//Busca um codigo pela string do seu codigo
	public CodigoPromocional buscar(String codigo) {
		return codigoRepository.findByCodigo(codigo);
	}
	
	//Busca um codigo pelo seu ID
	public CodigoPromocional buscar(Long id) {
		return codigoRepository.findOne(id);
	}
	
	//Lista todos os códigos
	public List<CodigoPromocional> listar() {
		return codigoRepository.findAll();
	}
	
	//Exclui um determinado codigo.
	public void excluir(Long id) {
		codigoRepository.delete(id);
	}
}
