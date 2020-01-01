package io.xream.reliable.listener;


import io.xream.reliable.bean.CatStatement;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.controller.StatementController;
import io.xream.x7.reliable.ReliableOnConsumed;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import io.xream.x7.common.bean.GenericObject;
import io.xream.x7.common.util.JsonX;

@Configuration
public class StatementListenerOfSettle {

    private static final Logger logger = LoggerFactory.getLogger(StatementListenerOfSettle.class);

    @Autowired
    private StatementController statementController;

    @ReliableOnConsumed(svc = "cat-statement")
    @KafkaListener(topics = "CAT_SETTLE_CREATED")
    public void onSettleCreated(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);

        GenericObject<CatStatement> go =  dto.getMessage().getBody();
        CatStatement catStatement = go.get();

        this.statementController.create(catStatement);

        logger.info("------------------------------------>>>>>>>>>>>>>>>>>>>>>>");
    }

}
