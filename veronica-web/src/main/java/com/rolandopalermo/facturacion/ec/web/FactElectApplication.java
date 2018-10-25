package com.rolandopalermo.facturacion.ec.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({"com.rolandopalermo.facturacion.ec"})
@PropertySource("classpath:data.properties")
public class FactElectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FactElectApplication.class, args);
	}

}