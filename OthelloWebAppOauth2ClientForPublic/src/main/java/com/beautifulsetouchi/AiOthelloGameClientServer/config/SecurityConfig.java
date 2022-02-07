package com.beautifulsetouchi.AiOthelloGameClientServer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.beautifulsetouchi.AiOthelloGameClientServer.logout.handler.OidcClientInitiatedLogoutSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/js/**","/css/**", "/images/**","/webjars/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.regexMatchers("\\/othello/board/graphic/normal\\?type=registered").authenticated()
			.regexMatchers("\\/othello/board/graphic/mican\\?type=registered").authenticated()			
			.regexMatchers("\\/othello/board/graphic/ai\\?type=registered").authenticated()
			.regexMatchers("\\/othello/about\\?type=registered").authenticated()
			.anyRequest().permitAll()
			.and()
			.oauth2Login()
			.and()
			.logout()
			.logoutSuccessHandler(oidcLogoutSuccessHandler())
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.deleteCookies("JSESSIONID");

	}
	
	private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler();
		successHandler.setPostLogoutRedirectUri("xxxx");
		successHandler.setEndSessionEndpoint("xxxx");
		return successHandler;
	}
	
}
