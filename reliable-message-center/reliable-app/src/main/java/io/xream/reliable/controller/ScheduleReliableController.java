/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.reliable.controller;

import io.xream.reliable.api.reliable.MessageResultService;
import io.xream.reliable.api.reliable.ReliableMessageService;
import io.xream.reliable.bean.constant.MessageStatus;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.bean.entity.MessageResult;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.produce.Producer;
import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.CriteriaBuilder;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import io.xream.x7.base.GenericObject;
import io.xream.x7.reliable.TCCTopic;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.xream.x7.base.GenericObject;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.base.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/schedule")

/**
 * @Author Sim
 */
public class ScheduleReliableController {

    @Autowired
    private ReliableMessageService reliableMessageService;
    @Autowired
    private MessageResultService messageResultService;

    @Autowired
    private Producer producer;

    @Resource(name = "nextProducer")
    private Producer nextProducer;

    @Autowired
    private TccBusiness tccBusiness;

    @Autowired
    private NextBusiness nextBusiness;


    @Value("${reliable.retry.duration:'5000'}")
    private long reliableRetryDuration;

    private long checkStatusDuration = 1400;


    @RequestMapping(value = "/tryToProduceNext",method = RequestMethod.GET)
    public boolean tryToProduceNext(){

        CriteriaBuilder builder = CriteriaBuilder.build(ReliableMessage.class);
        builder.and().eq("status",MessageStatus.NEXT);

        Criteria criteria = builder.get();

        List<ReliableMessage> list = this.reliableMessageService.listByCriteria(criteria);

        Map<String,List<ReliableMessage>> map = new HashMap<>();
        for (ReliableMessage reliableMessage : list) {
            String parentId = reliableMessage.getParentId();
            List<ReliableMessage> valueList = map.get(parentId);
            if (valueList == null) {
                valueList = new ArrayList<>();
                map.put(parentId,valueList);
            }
            valueList.add(reliableMessage);
        }

        for (String parentId : map.keySet()){
            ReliableMessage reliableMessage = this.reliableMessageService.get(parentId);
            if (reliableMessage.getStatus().equals(MessageStatus.OK.name())){
                this.nextBusiness.produce(parentId,reliableMessageService,nextProducer);
            }
        }

        return true;
    }

    @RequestMapping(value = "/tryToFinish", method = RequestMethod.GET)
    public boolean tryToFinish() {

        Date createAt = new Date(System.currentTimeMillis() - checkStatusDuration);

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("id").resultKey("svcDone").resultKey("svcList").resultKey("retryCount").resultKey("retryMax").resultKey("tcc").resultKey("body");
        builder.and().eq("status", MessageStatus.SEND);
        builder.and().lt("createAt", createAt);

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        List<Map<String, Object>> list = this.reliableMessageService.listByResultMap(resultMappedCriteria);

        if (list.isEmpty())
            return true;

        Date date = new Date();

        for (Map<String, Object> map : list) {

            List<String> svcList = (List<String>)MapUtils.getObject(map, "svcList");
            String tcc = MapUtils.getString(map, "tcc");
            Object bodyObj = MapUtils.getObject(map, "body");
            GenericObject go = (GenericObject) bodyObj;

            ReliableMessage reliableMessage = new ReliableMessage();
            reliableMessage.setId(MapUtils.getString(map, "id"));
            reliableMessage.setSvcDone(MapUtils.getString(map, "svcDone"));
            reliableMessage.setSvcList(svcList);
            reliableMessage.setBody(go);
            reliableMessage.setTcc(tcc);
            reliableMessage.setRetryCount(MapUtils.getLongValue(map, "retryCount"));
            reliableMessage.setRetryMax(MapUtils.getIntValue(map, "retryMax"));

            String svcDone = reliableMessage.getSvcDone();
            if (StringUtil.isNullOrEmpty(svcDone))
                continue;

            boolean flag = true;

            for (String svc : svcList) {
                flag &= svcDone.contains(svc);
            }


            if (reliableMessage.getTcc().equals(TCCTopic._TCC_TRY.name())) {
                if (!flag) {
                    if (reliableMessage.getRetryCount() >= reliableMessage.getRetryMax()) {
                        cancel(reliableMessage.getId());
                    }
                }
            } else {
                if (flag) {
                    RefreshCondition<ReliableMessage> reliableMessageRefreshCondition = new RefreshCondition<>();
                    reliableMessageRefreshCondition.refresh("status", MessageStatus.OK);
                    reliableMessageRefreshCondition.refresh("refreshAt", date);
                    reliableMessageRefreshCondition.and().eq("id", reliableMessage.getId());
                    this.reliableMessageService.refresh(reliableMessageRefreshCondition);

                    try {
                        this.nextBusiness.produce(reliableMessage.getId(),reliableMessageService,nextProducer);
                    }catch (Exception e) {

                    }
                }
            }

        }

        return true;
    }


    @RequestMapping(value = "/listForRetry", method = RequestMethod.GET)
    public List<ReliableMessage> listForRetry() {

        long rrd = reliableRetryDuration < 5000 ? 5000 : reliableRetryDuration;

        long now = System.currentTimeMillis();
        final long sendAt = now - rrd;

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("id").resultKey("svcList").resultKey("svcDone").resultKey("retryCount").resultKey("retryMax").resultKey("tcc").resultKey("topic").resultKey("body");
        builder.and().eq("status", MessageStatus.SEND);
//        builder.and().x("retryCount < retryMax"); //需要人工补单
        builder.and().lt("sendAt", sendAt);

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        List<Map<String, Object>> list = this.reliableMessageService.listByResultMap(resultMappedCriteria);

        List<ReliableMessage> rmList = new ArrayList<>();

        if (list.isEmpty())
            return rmList;

        for (Map<String, Object> map : list) {

            Object bodyObj = MapUtils.getObject(map, "body");
            GenericObject go = (GenericObject) bodyObj;

            List<String> svcList = (List<String>) MapUtils.getObject(map, "svcList");

            ReliableMessage reliableMessage = new ReliableMessage();
            reliableMessage.setId(MapUtils.getString(map, "id"));
            reliableMessage.setTopic(MapUtils.getString(map, "topic"));
            reliableMessage.setSvcList(svcList);
            reliableMessage.setSvcDone(MapUtils.getString(map, "svcDone"));
            reliableMessage.setRetryCount(MapUtils.getLongValue(map, "retryCount"));
            reliableMessage.setRetryMax(MapUtils.getIntValue(map, "retryMax"));
            reliableMessage.setBody(go);
            reliableMessage.setTcc(MapUtils.getString(map, "tcc"));
            reliableMessage.setSendAt(sendAt);

            rmList.add(reliableMessage);
        }

        return rmList;
    }

    @Transactional
    @RequestMapping(value = "/retry")
    public boolean retry(@RequestBody ReliableMessage reliableMessage) {

        Date date = new Date();

        if (reliableMessage.getRetryCount() < reliableMessage.getRetryMax()) {

            CriteriaBuilder builder = CriteriaBuilder.build(MessageResult.class);
            builder.and().eq("msgId", reliableMessage.getId());

            Criteria criteria = builder.get();

            List<MessageResult> list = this.messageResultService.listByCriteria(criteria);

            ReliableDto dto = new ReliableDto();
            for (MessageResult messageResult : list) {
                if (MessageStatus.BLANK.toString().equals(messageResult.getStatus())) {
                    dto.getResultList().add(messageResult);
                }
            }

            dto.setMessage(reliableMessage);

            reliableMessage.setRetryCount(reliableMessage.getRetryCount() + 1);
            reliableMessage.setSendAt(date.getTime());// IMPORTANT
            reliableMessage.setRefreshAt(date);

            RefreshCondition<ReliableMessage> refreshCondition = new RefreshCondition<>();
            refreshCondition.refresh("retryCount", reliableMessage.getRetryCount() + 1);
            refreshCondition.refresh("sendAt", reliableMessage.getSendAt());
            refreshCondition.refresh("refreshAt", reliableMessage.getRefreshAt());
            refreshCondition.and().eq("id", reliableMessage.getId());
            this.reliableMessageService.refresh(refreshCondition);

            /*
             * MQ
             */
            String topic = reliableMessage.getTopic();
            producer.send(topic, JsonX.toJson(dto));

        } else {

            if (reliableMessage.getTcc().equals(TCCTopic._TCC_TRY.name())) {
                // TODO: confirm step, exception no rollback
                cancel(reliableMessage.getId());
            } else {
                //进入人工补单审核流程
                RefreshCondition<ReliableMessage> refreshCondition = new RefreshCondition<>();
                refreshCondition.refresh("status", MessageStatus.FAIL);
                refreshCondition.refresh("refreshAt", date);
                refreshCondition.and().eq("id", reliableMessage.getId());
                this.reliableMessageService.refresh(refreshCondition);
            }

        }

        return true;
    }

    @RequestMapping(value = "/clean", method = RequestMethod.GET)
    public boolean clean() {

        List<String> cleanStatusList = new ArrayList<>();
        cleanStatusList.add(MessageStatus.OK.toString());
        cleanStatusList.add(MessageStatus.BLANK.toString());

        CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped();
        builder.resultKey("id");
        builder.and().eq("underConstruction", false);
        builder.and().in("status", cleanStatusList);

        Criteria.ResultMappedCriteria resultMappedCriteria = builder.get();

        List<Map<String, Object>> list = this.reliableMessageService.listByResultMap(resultMappedCriteria);


        for (Map<String, Object> map : list) {
            String id = MapUtils.getString(map, "id");
            if (StringUtil.isNullOrEmpty(id))
                continue;

            this.messageResultService.removeByMessageId(id);
            this.reliableMessageService.remove(id);
        }

        return true;
    }

    @Transactional
    public boolean cancel(String id) {
        ReliableMessage reliableMessage = this.reliableMessageService.get(id);
        return this.tccBusiness.cancel(reliableMessage, reliableMessageService, producer);
    }

}
