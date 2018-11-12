package com.rolandopalermo.facturacion.ec.web;

import com.rolandopalermo.facturacion.ec.config.SQSServiceConfig;
import com.rolandopalermo.facturacion.ec.manager.S3Manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({"com.rolandopalermo.facturacion.ec"})
@PropertySource("classpath:data.properties")
public class FactElectApplication {

	public static void main(String[] args) {

		SQSServiceConfig.getInstance().initialize();
		S3Manager.getInstance().initialize();
		
		SpringApplication.run(FactElectApplication.class, args);

	}

}