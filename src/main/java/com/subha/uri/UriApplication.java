package com.subha.uri;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Log
@EnableCaching
public class UriApplication {

	public static void main(String[] args) {
		SpringApplication.run(UriApplication.class, args);
	}

}
