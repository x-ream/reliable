package io.xream.reliable.config;

import io.xream.reliable.api.reliable.DtoConverter;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.x7.base.util.JsonX;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @Author Sim
 */
@Configuration
public class ReliableConfig {


    @Bean
    public DtoConverter dtoConverter() {

        return message -> {

            String body = ((ConsumerRecord<String, String>) message).value(); //KAFKA
            ReliableDto dto = JsonX.toObject(body, ReliableDto.class);

            return dto;
        };
    }
}
