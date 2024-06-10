package com.upside.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


//	    @Override
//	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	        registry.addResourceHandler("/image/**")
//	                .addResourceLocations("file:///C:/image/");
//	                
//	        registry.addResourceHandler("/image/profile/**")
//	                .addResourceLocations("file:///C:/image/profile/");
//	    }
	

	    @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        registry.addResourceHandler("/image/**")
	        		.addResourceLocations("file:///home/ec2-user/API-Server/image/");
	                
	        registry.addResourceHandler("/image/profile/**")
	        		.addResourceLocations("file:///home/ec2-user/API-Server/image/profile/");
	    }	
	
}
