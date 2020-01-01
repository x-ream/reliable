package io.xream.reliable.listener;

import io.xream.reliable.bean.CatSettle;
import io.xream.reliable.bean.CatStatement;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.controller.SettleController;
import io.xream.x7.reliable.ReliableOnConsumed;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import io.xream.x7.common.util.JsonX;

@Configuration
public class SettleListenerOfPayment {

    @Autowired
    private SettleController settleController;

    @ReliableOnConsumed(svc = "cat-settle", nextTopic = "CAT_SETTLE_CREATED", nextRetryMax = 2, nextSvcs = {"cat-statement"})
    @KafkaListener(topics = "CAT_PAID")
    public CatStatement onCatPaid(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatSettle catSettle = new CatSettle();
        catSettle.setId("CAT_SETTLE_TEST");
        this.settleController.create(catSettle);

        CatStatement catStatement = new CatStatement();
        catStatement.setId("TEST_STATEMENT");
        catStatement.setTest("CAT_SETTLE_CREATED");

        return catStatement;
    }

    @ReliableOnConsumed(svc = "cat-settle",nextTopic = "CAT_SETTLE_CREATED", nextRetryMax = 2, nextSvcs = {"cat-statement"})
    @KafkaListener(topics = "CAT_PAID_TCC_TRY")
    public CatStatement onCatPaid_TCC_TRY(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatSettle catSettle = new CatSettle();
        catSettle.setId("CAT_SETTLE_TEST");
        catSettle.setName("TRY");

        settleController.create(catSettle);

        CatStatement catStatement = new CatStatement();
        catStatement.setId("TEST_STATEMENT");
        catStatement.setTest("CAT_SETTLE_CREATED");

        return catStatement;

    }

    @ReliableOnConsumed(svc = "cat-settle")
    @KafkaListener(topics = "CAT_PAID_TCC_CONFIRM")
    public void onCatPaid_TCC_CONFIRM(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatSettle catsettle = new CatSettle();
        catsettle.setId("CAT_SETTLE_TEST");
        catsettle.setName("CONFIRM");

        settleController.confirm(catsettle);

    }

    @ReliableOnConsumed(svc = "cat-settle")
    @KafkaListener(topics = "CAT_PAID_TCC_CANCEL")
    public void onCatPaid_TCC_CANCEL(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatSettle catsettle = new CatSettle();
        catsettle.setId("CAT_SETTLE_TEST");
        catsettle.setName("CANCEL");

        settleController.cancel(catsettle);

    }


}
