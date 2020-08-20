package io.xream.reliable.controller;

import io.xream.reliable.bean.CatSettle;
import io.xream.reliable.repository.CatSettleRepository;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.xream.x7.common.web.ViewEntity;

@RestController
@RequestMapping("/settle")
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

        RefreshCondition<CatSettle> CatSettleRefreshCondition = new RefreshCondition<>();
        CatSettleRefreshCondition.refresh("name",catSettle.getName());
        CatSettleRefreshCondition.and().eq("id",catSettle.getId());
        repository.refresh(CatSettleRefreshCondition);

        return ViewEntity.ok();
    }

    @RequestMapping("/cancel")
    public ViewEntity cancel(CatSettle catSettle) {

        RefreshCondition<CatSettle> CatSettleRefreshCondition = new RefreshCondition<>();
        CatSettleRefreshCondition.refresh("name",catSettle.getName());
        CatSettleRefreshCondition.and().eq("id",catSettle.getId());
        repository.refresh(CatSettleRefreshCondition);

        return ViewEntity.ok();
    }
}
