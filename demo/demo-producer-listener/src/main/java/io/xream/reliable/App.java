package io.xream.reliable;

import io.xream.x7.EnableReyClient;
import io.xream.x7.reliable.EnableReliabilityManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import x7.EnableCorsConfig;
import x7.EnableDateToLongForJackson;
import x7.EnableX7Repository;


@SpringBootApplication
@EnableTransactionManagement
@EnableReliabilityManagement
@EnableX7Repository
@EnableDateToLongForJackson
@EnableReyClient
@EnableCorsConfig
public class App {

	
	public static void main(String[] args) {

		SpringApplication.run(App.class);
		
    }


}



