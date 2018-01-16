package com.hamburgueria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hamburgueria.model.Empresa;
import com.hamburgueria.repository.EmpresaRepository;

@Service
public class EmpresaService {

	@Autowired
	EmpresaRepository empresaRepository;
	
	public Empresa salvar(Empresa empresa) {
		return empresaRepository.save(empresa);
	}
	
	public Empresa buscar(Long id) {
		return empresaRepository.findOne(id);
	}
	
	public void excluir(Long id) {
		empresaRepository.delete(id);
	}
	
	public List<Empresa> listar(){
		return empresaRepository.findAll();
	}
}
