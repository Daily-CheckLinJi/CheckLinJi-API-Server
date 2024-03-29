package com.upside.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients // HttpClient 통신을 위한 어노테이션
@EnableAsync // 비동기 통신을 위한 어노테이션
@SpringBootApplication
public class CheckLinJiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckLinJiApiApplication.class, args);
		
	}

}
