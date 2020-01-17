package com.rolandopalermo.facturacion.ec.web;

import javax.sql.DataSource;

import com.rolandopalermo.facturacion.ec.manager.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan({"com.rolandopalermo.facturacion.ec"})
@PropertySource("classpath:data.properties")
public class FactElectApplication implements CommandLineRunner {

	@Autowired
	DataSource dataSource;

    @Value("${aws.sqs.queue}")
    private String sqsQueueName;

	public static void main(String[] args) {

		S3Manager.getInstance().initialize();

		SpringApplication.run(FactElectApplication.class, args);

	}
	
	@Override
    public void run(String ... args) throws Exception {
        System.out.println("DataSource = " + dataSource);
    }

	@Bean(name = "sqs_manager")
	public SQSManager createSQSManager() {
		return new SQSManager(sqsQueueName);
	}

}