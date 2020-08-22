package io.xream.reliable;

import io.xream.x7.EnableCorsConfig;
import io.xream.x7.EnableDateToLongForJackson;
import io.xream.x7.EnableReyClient;
import io.xream.x7.EnableX7Repository;
import io.xream.x7.reliable.EnableReliabilityManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableReliabilityManagement
@EnableX7Repository(baseTypeSupported = true)
@EnableDateToLongForJackson
@EnableReyClient
@EnableCorsConfig

/**
 * @Author Sim
 */
public class App {

	
	public static void main(String[] args) {

		SpringApplication.run(App.class);
		
    }


}



