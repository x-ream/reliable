package io.xream.reliable.listener;

import io.xream.reliable.bean.CatOrder;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.controller.OrderController;
import io.xream.x7.reliable.ReliableOnConsumed;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import x7.core.util.JsonX;

@Configuration
public class OrderListenerOfPayment {

    @Autowired
    private OrderController orderController;

    @ReliableOnConsumed(svc = "cat-order")
    @KafkaListener(topics = "CAT_PAID")
    public void onCatPaid(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatOrder catOrder = new CatOrder();
        catOrder.setId("CAT_ORDER_TEST");

        orderController.create(catOrder);

    }

    @ReliableOnConsumed(svc = "cat-order")
    @KafkaListener(topics = "CAT_PAID_TCC_TRY")
    public void onCatPaid_TCC_TRY(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatOrder catOrder = new CatOrder();
        catOrder.setId("CAT_ORDER_TEST");
        catOrder.setStatus("TRY");

        orderController.create(catOrder);

    }

    @ReliableOnConsumed(svc = "cat-order")
    @KafkaListener(topics = "CAT_PAID_TCC_CONFIRM")
    public void onCatPaid_TCC_CONFIRM(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatOrder catOrder = new CatOrder();
        catOrder.setId("CAT_ORDER_TEST");
        catOrder.setStatus("CONFIRM");

        orderController.confirm(catOrder);

    }

    @ReliableOnConsumed(svc = "cat-order")
    @KafkaListener(topics = "CAT_PAID_TCC_CANCEL")
    public void onCatPaid_TCC_CANCEL(ConsumerRecord<String, String> record) {

        String json = record.value();
        ReliableDto dto = JsonX.toObject(json,ReliableDto.class);
        //--------------

        CatOrder catOrder = new CatOrder();
        catOrder.setId("CAT_ORDER_TEST");
        catOrder.setStatus("CANCEL");

        orderController.cancel(catOrder);

    }
}
