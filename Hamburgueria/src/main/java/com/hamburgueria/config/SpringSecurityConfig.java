package com.hamburgueria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProviderHamburgueria authProvider;
	
	@Autowired
	private LoginSuccessHandler loginSuccessHandler;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .authorizeRequests()
        	.antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
            .antMatchers("/usuario/cadastrar", "/usuario/starter", "/usuario/recuperarsenha",
            		"/usuario/alterarsenha/**", "/usuario/novasenha").permitAll()
            .antMatchers("/sede/**").hasRole("MASTER")
            .antMatchers("/usuario/listar_usuarios", "/usuario/perfil_usuario/**", "/usuario/editar_papel/**",
            		"/usuario/consumo/**", "/usuario/listar_produtos/**", 
            		"/codigo/cadastrar", "/codigo/editar/**", "/codigo/excluir/**").hasAnyRole("MASTER", "ADMINISTRADOR")
            .anyRequest().authenticated()
            .and()
        .exceptionHandling()
            .accessDeniedPage("/negado")
            .and()
        .formLogin()
            .loginPage("/")
            .usernameParameter("email")
            .passwordParameter("senha")
            .successHandler(loginSuccessHandler).permitAll()
            .failureUrl("/erroLogin")
            .permitAll()
            .and()
        .logout()
        	.logoutRequestMatcher(new AntPathRequestMatcher("/usuario/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .permitAll()
            .and().httpBasic();
	}
}
