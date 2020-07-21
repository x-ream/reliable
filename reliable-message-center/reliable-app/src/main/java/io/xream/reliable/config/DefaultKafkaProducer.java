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
package io.xream.reliable.config;

import io.xream.reliable.bean.exception.ReliableExceptioin;
import io.xream.reliable.produce.Producer;
import io.xream.x7.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class DefaultKafkaProducer implements Producer {


    private final static Logger logger = LoggerFactory.getLogger(DefaultKafkaProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public DefaultKafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public boolean send(String topic, String message) {

        try {
            kafkaTemplate.send(topic, message).get();
            if (logger.isDebugEnabled()) {
                logger.debug("Kafka produce, topic = {}, data = {}", topic, message);
            }
        }catch (Throwable ex) {
            String str = ExceptionUtil.getMessage(ex);
            logger.error("Kafka produce error, ex = {}, topic = {}, data = {}", str, topic, message);
            throw new ReliableExceptioin(str);
        }
        return true;
    }
}
