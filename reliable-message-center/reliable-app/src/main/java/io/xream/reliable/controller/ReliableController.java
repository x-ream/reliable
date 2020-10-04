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
import io.xream.reliable.bean.dto.ConsumedReliableDto;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.bean.entity.MessageResult;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.bean.exception.ReliableExceptioin;
import io.xream.reliable.produce.Producer;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.base.util.StringUtil;
import io.xream.x7.reliable.TCCTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
@RestController
@RequestMapping("/message")

/**
 * @Author Sim
 */
public class ReliableController {


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


    @RequestMapping("/create")
    public ReliableDto create(@RequestBody ReliableDto dto) {

        ReliableMessage reliableMessage = dto.getMessage();
        if (reliableMessage == null)
            throw new ReliableExceptioin("reliableMessage == null");

        Date date = new Date();

        String messageId = reliableMessage.getId();
        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
            messageId = messageId.replace("-", "");
        }

        reliableMessage.setId(messageId);
        reliableMessage.setCreateAt(date);
        reliableMessage.setSendAt(date.getTime());
        reliableMessage.setSvcDone(TccBusiness.SVC_DONE_PREFIX);

        if (StringUtil.isNullOrEmpty(reliableMessage.getTracingId())) {
            reliableMessage.setTracingId(messageId);
        }

        if (StringUtil.isNullOrEmpty(reliableMessage.getStatus())){
            reliableMessage.setStatus(MessageStatus.BLANK.toString());
        }

        List<MessageResult> resultList = dto.getResultList();

        this.reliableMessageService.create(reliableMessage);

        for (MessageResult result : resultList) {

            result.setId(result.getSvc() + "_" + messageId);
            result.setMsgId(messageId);
            result.setStatus(MessageStatus.BLANK.toString());
            result.setCreateAt(date);

            this.messageResultService.create(result);
        }

        return dto;
    }


    @RequestMapping("/produce")
    public boolean produce(@RequestBody  ReliableDto dto) {

        ReliableMessage reliableMessage = dto.getMessage();
        if (reliableMessage == null)
            throw new ReliableExceptioin("reliableMessage == null");

        Date date = new Date();
        reliableMessage.setRefreshAt(date);
        reliableMessage.setStatus(MessageStatus.SEND.toString());

        boolean flag = this.reliableMessageService.refresh(
                RefreshCondition.build()
                        .refresh("status",reliableMessage.getStatus())
                        .refresh("sendAt", reliableMessage.getSendAt())
                        .refresh("refreshAt", reliableMessage.getRefreshAt())
                        .eq("id", reliableMessage.getId())
        );

        if (!flag)
            throw new ReliableExceptioin("reliableMessage refresh persist failed");

        /*
         * MQ
         */
        String topic = reliableMessage.getTopic();
        return producer.send(topic, JsonX.toJson(dto));
    }


    @RequestMapping("/consume")
    public boolean consume(@RequestBody  ConsumedReliableDto dto) {

        String msgId = dto.getMsgId();
        String svc = dto.getSvc();
        if (StringUtil.isNullOrEmpty(msgId))
            throw new ReliableExceptioin("ConsumedReliableDto lack of msgId: " + dto);

        if (StringUtil.isNullOrEmpty(svc))
            return true;

        Date date = new Date();

        String resultId = dto.getResultId();
        if (StringUtil.isNotNull(resultId)) {

            boolean flag = this.messageResultService.refresh(
                    RefreshCondition.build()
                            .refresh("status", dto.getTcc())
                            .refresh("refreshAt", date)
                            .eq("id", resultId).eq("status", MessageStatus.BLANK)
            );
            if (!flag)
                throw new ReliableExceptioin("Problem with refresh resultMessage, id = " + resultId);
        }else {
            String id = svc + "_" + msgId;
            if (id.length() > 55) {
                id = id.substring(0,55);
            }
            MessageResult messageResult = new MessageResult();
            messageResult.setId(id);
            messageResult.setMsgId(msgId);
            messageResult.setStatus(dto.getTcc());
            messageResult.setSvc(svc);
            messageResult.setCreateAt(date);
            messageResult.setRefreshAt(date);
            try {
                boolean flag = this.messageResultService.create(messageResult);
                if (!flag)
                    throw new ReliableExceptioin("Problem with create resultMessage, id = " + id);
            }catch (Exception e){
                throw new ReliableExceptioin("Problem with create resultMessage, id = " + id);
            }
        }

        return this.reliableMessageService.refresh(
                RefreshCondition.build()
                        .refresh("svcDone = CONCAT(svcDone, ? , '" + TccBusiness.SVC_DONE_PREFIX +"' )", svc)
                        .refresh("refreshAt", date)
                        .eq("id", msgId)
                        .eq("tcc", dto.getUseTcc() ? dto.getTcc() : null)
        );

    }


    @RequestMapping("/tryToConfirm")
    public boolean tryToConfirm(@RequestBody String msgId) {
        ReliableMessage message = this.reliableMessageService.get(msgId);
        if (message == null)
            return true;

        for (Object svc : message.getSvcList()){
            if (!message.getSvcDone().contains(svc.toString()))
                return false;
        }

        if (message.getTcc().equals(TCCTopic._TCC_NONE.name()))
            return true;

        boolean flag = this.tccBusiness.confirm(message,reliableMessageService,producer);

        if (!flag)
            throw new RuntimeException("ERROR, at ReliableProducer TCC confirm");

        try {
            this.nextBusiness.produce(message.getId(), reliableMessageService, nextProducer);
        }catch (Exception e) {
            // 需要任务补偿
        }

        return flag;
    }


    @RequestMapping("/cancel")
    public boolean cancel(@RequestBody String msgId) {

        ReliableMessage message = this.reliableMessageService.get(msgId);
        if (message == null)
            return false;
        if (message.getTcc().equals(TCCTopic._TCC_NONE.name()))
            return false;

        return this.tccBusiness.cancel(message,reliableMessageService,producer);
    }


}
