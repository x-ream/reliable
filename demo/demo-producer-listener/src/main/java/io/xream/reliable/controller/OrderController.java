package io.xream.reliable.controller;


import io.xream.reliable.bean.CatOrder;
import io.xream.reliable.repository.CatOrderRepository;
import io.xream.sqli.builder.RefreshBuilder;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequestMapping("/order")

/**
 * @Author Sim
 */
public class OrderController {

    @Autowired
    private CatOrderRepository repository;

    @RequestMapping("/create")
    public ViewEntity create(@RequestBody CatOrder order) {

        repository.create(order);

        return ViewEntity.ok();
    }

    @RequestMapping("/confirm")
    public ViewEntity confirm(CatOrder catOrder) {

        repository.refresh(
                RefreshBuilder.builder()
                        .refresh("status",catOrder.getStatus())
                        .eq("id",catOrder.getId()).build()

        );

        return ViewEntity.ok();
    }

    @RequestMapping("/cancel")
    public ViewEntity cancel(CatOrder catOrder) {

        repository.refresh(RefreshBuilder.builder()
                .refresh("status",catOrder.getStatus())
                .eq("id",catOrder.getId()).build()
        );

        return ViewEntity.ok();
    }
}
