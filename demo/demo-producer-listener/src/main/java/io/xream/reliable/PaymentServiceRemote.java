package io.xream.reliable;

import io.xream.reliable.bean.Cat;
import io.xream.x7.annotation.ReyClient;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@ReyClient(value = "${reliable.demo}/payment")

/**
 * @Author Sim
 */
public interface PaymentServiceRemote {

    @RequestMapping("/pay")
    ViewEntity pay(Cat cat);
}
