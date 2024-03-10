package com.restapi.vinted;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VintedApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	public static void main(String[] args) {
		SpringApplication.run(VintedApplication.class, args);
	}

}
//todo: dodać commandLineRunnera - żeby automatycznie zostały utworzone 2 role dla użytkowników
//todo: fajnie by było, jakby byli też automatycznie tworzeni użytkownicy: user i admin
// admin tworzyłby kategorie (CATEGORIES.txt),
// a użytkownik dodawałby wszystkie ubrania ze swojego konta.
