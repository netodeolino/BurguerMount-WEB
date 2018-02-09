package com.hamburgueria.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler{
	
	RedirectStrategy redirect = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		for (GrantedAuthority auth : authentication.getAuthorities()) {
			if(auth.getAuthority().contains("FUNCIONARIO")) {
				redirect.sendRedirect(request, response, "/pedido/listar/todos");
			}else if(auth.getAuthority().contains("ADMINISTRADOR")) {
				redirect.sendRedirect(request, response, "/tipo_ingrediente/listar");
			}else if(auth.getAuthority().contains("MASTER")) {
				redirect.sendRedirect(request, response, "/sede/listar");
			}else {
				redirect.sendRedirect(request, response, "/usuario/logout");
			}
		}
    }	
	
	
}
