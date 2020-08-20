package io.xream.reliable.config;

import io.xream.reliable.produce.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@Configuration

/**
 * @Author Sim
 */
public class KafkaConfig {

    @Autowired
    private NextKafkaProperties nextKafkaProperties;

    @Bean(name = "producer")
    @Primary
    @Order(2)
    public Producer producer(){
        return new ProducerWrapper();
    }

    @Bean(name = "nextProducer")
    @Order(3)
    public Producer nextProducer() {

        NextKafkaProducer nextKafkaProducer = new NextKafkaProducer();
        nextKafkaProducer.init(this.nextKafkaProperties.getProducer().buildProperties());

        return nextKafkaProducer;
    }

}
