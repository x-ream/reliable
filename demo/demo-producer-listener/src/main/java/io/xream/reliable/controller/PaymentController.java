package io.xream.reliable.controller;


import io.xream.reliable.bean.Cat;
import io.xream.reliable.repository.CatRepository;
import io.xream.x7.reliable.ReliableProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.xream.x7.base.web.ViewEntity;

@Transactional
@RestController
@RequestMapping("/payment")

/**
 * @Author Sim
 */
public class PaymentController  {

    @Autowired
    private CatRepository repository;

    @ReliableProducer(useTcc=true,topic = "CAT_PAID",  svcs = {"cat-order","cat-settle"})
    @RequestMapping("/pay")
    public ViewEntity pay(@RequestBody Cat cat) {

        repository.create(cat);

        return ViewEntity.ok();
    }
}
