package com.bolsadeideas.springboot.app;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
/*
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		final Logger log = LoggerFactory.getLogger(getClass());
		
		String resourcePath = Paths.get("upload").toAbsolutePath().toUri().toString();
		log.info("resourcePath=> " + resourcePath);
		registry.addResourceHandler("/upload/**")
		.addResourceLocations(resourcePath);
	}
	
	*/

}
