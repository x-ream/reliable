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

import io.xream.reliable.api.reliable.ReliableMessageService;
import io.xream.reliable.bean.constant.MessageStatus;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.produce.Producer;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.x7.base.GenericObject;
import io.xream.x7.base.util.JsonX;
import io.xream.x7.reliable.TCCTopic;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component

/**
 * @Author Sim
 */
public class TccBusiness {

    public final static String SVC_DONE_PREFIX = "&";


    public boolean confirm(ReliableMessage reliableMessage, ReliableMessageService reliableMessageService, Producer producer) {

        Date date = new Date();

        boolean flag = reliableMessageService.refresh(
                RefreshCondition.build()
                        .refresh("status", MessageStatus.OK)
                        .refresh("refreshAt", date)
                        .refresh("tcc", TCCTopic._TCC_CONFIRM)
                        .eq("id", reliableMessage.getId())
                        .eq("tcc", TCCTopic._TCC_TRY)
        ); //STEP 1

        if (!flag)
            return flag;

        GenericObject body = reliableMessage.getBody();
        String topic = reliableMessage.getTopic();
        String tracingId = reliableMessage.getId();
        ReliableMessage reliableMessageConfirm = new ReliableMessage(tracingId,topic,body,reliableMessage.getSvcList());
        reliableMessageConfirm.setRetryMax(3 * 2);
        reliableMessageConfirm.resetTopic(TCCTopic._TCC_CONFIRM);

        String messageId = UUID.randomUUID().toString();
        messageId = messageId.replace("-","");

        reliableMessageConfirm.setId(messageId);
        reliableMessageConfirm.setCreateAt(date);
        reliableMessageConfirm.setRefreshAt(date);
        reliableMessageConfirm.setSendAt(date.getTime());
        reliableMessageConfirm.setSvcDone(SVC_DONE_PREFIX);
        reliableMessageConfirm.setStatus(MessageStatus.SEND.toString());//初始化为已发送
        reliableMessageConfirm.setTcc(TCCTopic._TCC_CONFIRM.name());

        boolean b = reliableMessageService.create(reliableMessageConfirm); //STEP 2
        if (b){
            ReliableDto dto = new ReliableDto(reliableMessageConfirm);
            b &= producer.send(reliableMessageConfirm.getTopic(), JsonX.toJson(dto)); //STEP 3
        }

        return b;
    }

    public boolean cancel(ReliableMessage reliableMessage, ReliableMessageService reliableMessageService, Producer producer) {

        Date date = new Date();

        boolean flag = reliableMessageService.refresh(
                RefreshCondition.build()
                        .refresh("status", MessageStatus.FAIL)
                        .refresh("refreshAt", date)
                        .refresh("tcc", TCCTopic._TCC_CANCEL)
                        .eq("id", reliableMessage.getId())
                        .eq("svcDone",reliableMessage.getSvcDone())
                        .ne("tcc", TCCTopic._TCC_CANCEL)
        ); //STEP 1

        if (!flag)
            return flag;

        GenericObject body = reliableMessage.getBody();
        String topic = reliableMessage.getTopic();
        String tracingId = reliableMessage.getId();
        ReliableMessage reliableMessageCancel = new ReliableMessage(tracingId,topic,body,reliableMessage.getSvcList());
        reliableMessageCancel.setRetryMax(3 * 2);
        reliableMessageCancel.resetTopic(TCCTopic._TCC_CANCEL);

        String messageId = UUID.randomUUID().toString();
        messageId = messageId.replace("-","");

        reliableMessageCancel.setId(messageId);
        reliableMessageCancel.setCreateAt(date);
        reliableMessageCancel.setRefreshAt(date);
        reliableMessageCancel.setSendAt(date.getTime());
        reliableMessageCancel.setSvcDone(SVC_DONE_PREFIX);
        reliableMessageCancel.setStatus(MessageStatus.SEND.toString());//初始化为已发送
        reliableMessageCancel.setTcc(TCCTopic._TCC_CANCEL.name());

        boolean b = reliableMessageService.create(reliableMessageCancel); //STEP 2

        if (b){
            ReliableDto dto = new ReliableDto(reliableMessageCancel);
            b &= producer.send(reliableMessageCancel.getTopic(), JsonX.toJson(dto)); //STEP 3
        }

        return b;
    }
}
