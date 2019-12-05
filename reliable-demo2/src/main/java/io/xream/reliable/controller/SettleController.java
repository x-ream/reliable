package io.xream.reliable.controller;

import io.xream.reliable.bean.CatSettle;
import io.xream.reliable.repository.CatSettleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.ViewEntity;

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
