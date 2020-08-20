package io.xream.reliable;

import io.xream.reliable.bean.Cat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @Author Sim
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {


    private static Logger logger = LoggerFactory.getLogger(AppTest.class);

    @Autowired
    private PaymentServiceRemote paymentServiceRemote;

    @Test
    public void test(){

        Cat cat = new Cat();
        cat.setId("CAT_TEST_SSSSS");
        cat.setName("ddse");
        cat.setDesc("RWER");

        this.paymentServiceRemote.pay(cat);

    }
}
