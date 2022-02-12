package com.beautifulsetouchi.AiOthelloGameClientServer.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * 画面表示されるメッセージの国際化対応を行うクラス
 * @author shunyu
 *
 */
@Configuration
public class MessageConfig {
	
	@Bean
	public MessageSource messageSource() {
		var source = new ReloadableResourceBundleMessageSource();
		source.setBasenames(
				"classpath:i18n/messages",
				"classpath:i18n/ValidationMessages"
				);
		source.setDefaultEncoding("UTF-8");
		source.setFallbackToSystemLocale(false);
		
		return source;
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
		var bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());
		
		return bean;
	}

}
