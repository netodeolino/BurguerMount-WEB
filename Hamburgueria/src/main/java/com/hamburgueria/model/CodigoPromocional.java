package com.hamburgueria.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class CodigoPromocional {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String codigo;
	private Integer creditos;
	private Integer maxUso;
	
	@OneToMany
	private List<Usuario> usuarios;
	
	public CodigoPromocional() {
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getCreditos() {
		return creditos;
	}
	public void setCreditos(Integer creditos) {
		this.creditos = creditos;
	}
	public Integer getMaxUso() {
		return maxUso;
	}
	public void setMaxUso(Integer maxUso) {
		this.maxUso = maxUso;
	}
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	public String gerarCodigo() {		
		String uuid = UUID.randomUUID().toString();
		String codigo = String.valueOf(uuid);
		return codigo.substring(0, 6).toUpperCase();
	}
	
}
