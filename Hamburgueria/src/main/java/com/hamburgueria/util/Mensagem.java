package com.hamburgueria.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mensagem {
	
	//Cria uma lista com várias mensagens;
	public List<String> povoarMensagens(){
		List<String> mensagens = new ArrayList();
		
		mensagens.add("Obrigado pela preferência.");
		mensagens.add("Esperamos vê-lo aqui mais uma vez, em breve!");
		mensagens.add("Obrigado por comprar com a gente!");
		mensagens.add("Obrigado por fazer parte da nossa história!");
		mensagens.add("Ótima refeição!");
		mensagens.add("Não deixe de conferir nossas incríveis promoções no Instagram @burguermount!");
		mensagens.add("Indique amigos para a BurguerMount e ganhe créditos especiais :)");
		
		return mensagens;
	}
	
	//Sorteia e retorna uma mensagem aleatória.
	public String getMensagem() {
		
		List<String> mensagens = this.povoarMensagens();
		
		Random aleatorio = new Random();
		
		return mensagens.get(aleatorio.nextInt(mensagens.size()));
	}
	
}
