package io.xream.reliable;

import io.xream.reliable.bean.Cat;
import io.xream.x7.reyc.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import io.xream.x7.common.web.ViewEntity;

@ReyClient(value = "${reliable.demo}/payment")

/**
 * @Author Sim
 */
public interface PaymentServiceRemote {

    @RequestMapping("/pay")
    ViewEntity pay(Cat cat);
}
