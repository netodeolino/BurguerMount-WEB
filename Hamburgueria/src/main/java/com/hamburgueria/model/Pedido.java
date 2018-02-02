package com.hamburgueria.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Date data;
	private String local;
	private Double preço;
	private Double dinheiroCliente;
	private String mensagem;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@ManyToOne
	private Usuario cliente;
	
	@OneToMany
	private List<Ingrediente> ingredientes;
	
	public Pedido() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Double getPreço() {
		return preço;
	}

	public void setPreço(Double preço) {
		this.preço = preço;
	}

	public Double getDinheiroCliente() {
		return dinheiroCliente;
	}

	public void setDinheiroCliente(Double dinheiroCliente) {
		this.dinheiroCliente = dinheiroCliente;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}
	
}
