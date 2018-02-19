package com.hamburgueria.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.hamburgueria.util.Constants;

@Entity
public class TipoIngrediente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	
	@Column(columnDefinition = "text", length = Constants.TAM_MAX_IMG_64)
	private String foto64;
	
	@OneToMany
	private List<Ingrediente> ingredientes;
	
	@ManyToOne
	private Sede sede;
	
	public TipoIngrediente() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getFoto64() {
		return foto64;
	}

	public void setFoto64(String foto64) {
		this.foto64 = foto64;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}

	public Sede getSede() {
		return sede;
	}

	public void setSede(Sede sede) {
		this.sede = sede;
	}
		
}
