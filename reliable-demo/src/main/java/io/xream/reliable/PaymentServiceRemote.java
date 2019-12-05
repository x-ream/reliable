package io.xream.reliable;

import io.xream.reliable.bean.Cat;
import io.xream.x7.reyc.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;
import x7.core.web.ViewEntity;

@ReyClient(value = "${reliable.demo}/payment")
public interface PaymentServiceRemote {

    @RequestMapping("/pay")
    ViewEntity pay(Cat cat);
}
