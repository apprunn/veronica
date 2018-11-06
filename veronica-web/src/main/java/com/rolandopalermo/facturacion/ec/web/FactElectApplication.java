package com.rolandopalermo.facturacion.ec.web;

import com.rolandopalermo.facturacion.ec.bo.repository.CompanyRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.rolandopalermo.facturacion.ec"})
@EntityScan({"com.rolandopalermo.facturacion.ec.domain"})
@PropertySource("classpath:data.properties")
@EnableJpaRepositories(basePackages="com.rolandopalermo.facturacion.ec.bo.repository")
public class FactElectApplication {

	private static final Logger log = LoggerFactory.getLogger(FactElectApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FactElectApplication.class, args);
	}

}