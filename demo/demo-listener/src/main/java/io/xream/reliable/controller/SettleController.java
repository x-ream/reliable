package io.xream.reliable.controller;

import io.xream.reliable.bean.CatSettle;
import io.xream.reliable.repository.CatSettleRepository;
import io.xream.sqli.builder.RefreshBuilder;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settle")

/**
 * @Author Sim
 */
public class SettleController {

    @Autowired
    private CatSettleRepository repository;

    @RequestMapping("/create")
    public ViewEntity create(@RequestBody CatSettle catSettle) {
        this.repository.create(catSettle);

        return ViewEntity.ok();
    }

    @RequestMapping("/confirm")
    public ViewEntity confirm(CatSettle catSettle) {

        repository.refresh(
                RefreshBuilder.builder()
                        .refresh("name",catSettle.getName())
                        .eq("id",catSettle.getId()).build()
        );

        return ViewEntity.ok();
    }

    @RequestMapping("/cancel")
    public ViewEntity cancel(CatSettle catSettle) {

        repository.refresh(
                RefreshBuilder.builder()
                        .refresh("name",catSettle.getName())
                        .eq("id",catSettle.getId()).build()
        );

        return ViewEntity.ok();
    }
}
