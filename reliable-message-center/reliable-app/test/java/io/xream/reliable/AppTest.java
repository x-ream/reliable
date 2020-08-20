package io.xream.reliable;


import io.xream.reliable.produce.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)

/**
 * @Author Sim
 */
public class AppTest {

    @Autowired
    private Producer producer;

    @Resource(name = "nextProducer")
    private Producer nextProducer;

    @Test
    public void testAll(){

        System.out.println(this.producer);
        System.out.println(this.nextProducer);

    }

}
