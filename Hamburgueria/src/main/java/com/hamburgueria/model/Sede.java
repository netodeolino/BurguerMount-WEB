package com.hamburgueria.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Sede {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String cidade;
	
	@ManyToOne
	private Empresa empresa;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Ingrediente> estoque;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Pedido> pedidos;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Produto> produtos;
	
	@OneToMany
	private List<Usuario> clientes;

	public Sede() {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<Ingrediente> getEstoque() {
		return estoque;
	}

	public void setEstoque(List<Ingrediente> estoque) {
		this.estoque = estoque;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public List<Usuario> getClientes() {
		return clientes;
	}

	public void setClientes(List<Usuario> clientes) {
		this.clientes = clientes;
	}
	
}
