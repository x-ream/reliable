/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.reliable;

import io.xream.reliable.config.ProducerCustomizer;
import io.xream.reliable.produce.Producer;
import io.xream.x7.EnableReyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import x7.EnableCorsConfig;
import x7.EnableDateToLongForJackson;
import x7.EnableTransactionManagementReadable;
import x7.EnableX7Repository;


@SpringBootApplication
@EnableTransactionManagement
@EnableTransactionManagementReadable
@EnableX7Repository
@EnableDateToLongForJackson
@EnableReyClient
@EnableCorsConfig
public class App {

	
	public static void main(String[] args) {

		SpringApplication.run(App.class);
		
    }


//    @Bean
//	public ProducerCustomizer producerCustomizer(){
//
//		return new ProducerCustomizer() {
//			@Override
//			public Producer customize() {
//				return null; //Default is io.xream.reliable.config.KafkaProducer, you can use RocketMQ,RabbitMQ ....
//			}
//		};
//	}

}



