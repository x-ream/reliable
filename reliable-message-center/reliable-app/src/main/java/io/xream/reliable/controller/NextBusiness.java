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
import io.xream.reliable.bean.exception.ReliableExceptioin;
import io.xream.reliable.produce.Producer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.CriteriaBuilder;
import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.common.util.JsonX;

import java.util.Date;
import java.util.List;

@Component
public class NextBusiness {

    @Transactional
    public boolean produce(String parentId, ReliableMessageService reliableMessageService, Producer producer){

        CriteriaBuilder builder = CriteriaBuilder.build(ReliableMessage.class);
        builder.and().eq("parentId",parentId);
        builder.and().eq("status",MessageStatus.NEXT);
        Criteria criteria = builder.get();

        List<ReliableMessage> list = reliableMessageService.listByCriteria(criteria);

        Date date = new Date();

        for (ReliableMessage reliableMessage : list) {
            RefreshCondition<ReliableMessage> condition = new RefreshCondition<>();
            condition.refresh("status",MessageStatus.SEND);
            condition.refresh("sendAt",date.getTime());
            condition.refresh("refreshAt", date);
            condition.and().eq("id", reliableMessage.getId());
            reliableMessageService.refresh(condition);
        }

        for (ReliableMessage reliableMessage : list) {
            ReliableDto dto = new ReliableDto();
            dto.setMessage(reliableMessage);
            String message = JsonX.toJson(dto);
            String topic = reliableMessage.getTopic();
            boolean flag = producer.send(topic, message);
            if (!flag) {
                throw new ReliableExceptioin("Next produce failed， topic: " + topic + ", message: " + message);
            }
        }

        return true;
    }
}
