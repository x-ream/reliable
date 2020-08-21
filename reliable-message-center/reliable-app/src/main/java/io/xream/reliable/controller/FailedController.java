package io.xream.reliable.controller;

import io.xream.reliable.api.reliable.FailedService;
import io.xream.reliable.bean.constant.MessageStatus;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.sqli.page.Direction;
import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.CriteriaBuilder;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/failed")

/**
 * @Author Sim
 */
public class FailedController {

    @Autowired
    private FailedService failedService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public List<Map<String,Object>> findFailed() {

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("id").resultKey("status").resultKey("retryMax").resultKey("topic");
        builder.and().eq("status", MessageStatus.FAIL);
        builder.and().gt("retryMax", 0);
        builder.paged().sort("topic", Direction.DESC);

        Criteria.ResultMappedCriteria criteria = builder.get();

        List<Map<String,Object>> mapList = this.failedService.listByResultMap(criteria);

        return mapList;
    }

    @RequestMapping(value = "/find/{topic}", method = RequestMethod.GET)
    public List<Map<String,Object>> findFailedByTopic(@PathVariable String topic) {

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("id").resultKey("status").resultKey("retryMax").resultKey("topic");
        builder.and().eq("status", MessageStatus.FAIL);
        builder.and().eq("topic",topic);
        builder.and().gt("retryMax", 0);

        Criteria.ResultMappedCriteria criteria = builder.get();

        List<Map<String,Object>> mapList = this.failedService.listByResultMap(criteria);

        return mapList;
    }

    @RequestMapping(value = "/retryAll", method = RequestMethod.GET)
    public boolean retryAll(){

        RefreshCondition<ReliableMessage> refreshCondition = new RefreshCondition<>();
        refreshCondition.refresh("status",MessageStatus.SEND).refresh("retryCount",0);
        refreshCondition.and().eq("status",MessageStatus.FAIL);
        refreshCondition.and().gt("retryMax",0);

        return this.failedService.refreshUnSafe(refreshCondition);
    }

    @RequestMapping(value = "/retry/{messageId}", method = RequestMethod.GET)
    public boolean retry(@PathVariable String messageId){

        RefreshCondition<ReliableMessage> refreshCondition = new RefreshCondition<>();
        refreshCondition.refresh("status",MessageStatus.SEND).refresh("retryCount",0);
        refreshCondition.and().eq("id",messageId);

        return this.failedService.refresh(refreshCondition);
    }

}
