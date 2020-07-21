package io.xream.reliable.config;

import io.xream.reliable.produce.Producer;


public class ProducerWrapper implements Producer {

    private Producer producer;
    public void setProducer(Producer producer) {
        this.producer = producer;
    }
    @Override
    public boolean send(String topic, String message) {
        return producer.send(topic, message);
    }
}
