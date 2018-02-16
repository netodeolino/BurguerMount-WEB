package com.hamburgueria.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mensagem {
	
	//Cria uma lista com várias mensagens;
	public List<String> povoarMensagens(){
		List<String> mensagens = new ArrayList();
		
		mensagens.add("Obrigado pela preferência.");
		mensagens.add("2");
		mensagens.add("3");
		mensagens.add("4");
		mensagens.add("5");
		mensagens.add("6");
		mensagens.add("7");
		
		return mensagens;
	}
	
	//Sorteia e retorna uma mensagem aleatória.
	public String getMensagem() {
		
		List<String> mensagens = this.povoarMensagens();
		
		Random aleatorio = new Random();
		
		return mensagens.get(aleatorio.nextInt(mensagens.size()));
	}
	
}
