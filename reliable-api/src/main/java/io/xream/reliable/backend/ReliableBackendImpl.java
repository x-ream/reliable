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
package io.xream.reliable.backend;

import io.xream.reliable.api.reliable.DtoConverter;
import io.xream.reliable.bean.constant.MessageStatus;
import io.xream.reliable.bean.dto.ConsumedReliableDto;
import io.xream.reliable.bean.dto.ReliableDto;
import io.xream.reliable.bean.entity.MessageResult;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.bean.exception.ReliableExceptioin;
import io.xream.reliable.remote.reliable.ReliableServiceRemote;
import io.xream.x7.base.GenericObject;
import io.xream.x7.base.util.ExceptionUtil;
import io.xream.x7.reliable.TCCTopic;
import io.xream.x7.reliable.api.MessageTraceable;
import io.xream.x7.reliable.api.ReliableBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author Sim
 */
public class ReliableBackendImpl implements ReliableBackend {

    @Autowired
    private ReliableServiceRemote reliableServiceRemote;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    @Transactional
    public Object produceReliably(Boolean useTcc, String id, int retryMax, boolean underConstruction, String topic, Object body, MessageTraceable MessageTraceable, String[] svcs, Callable callable) {

        String tracingId = null;
        if (MessageTraceable != null){
            tracingId = MessageTraceable.getTracingId();
        }
        ReliableMessage reliableMessage = new ReliableMessage(id,retryMax,underConstruction,tracingId,topic, body, Arrays.asList(svcs));
        reliableMessage.setTcc(useTcc ? TCCTopic._TCC_TRY.name() : TCCTopic._TCC_NONE.name());
        if (useTcc) {
            reliableMessage.resetTopic(TCCTopic._TCC_TRY);
        }
        ReliableDto dto = new ReliableDto(reliableMessage);
        dto = this.reliableServiceRemote.create(dto); //STEP 1

        Object result = null;
        try {
            result = callable.call(); //STEP 2
        } catch (Exception e) {
            throw new ReliableExceptioin(ExceptionUtil.getMessage(e));
        }

        this.reliableServiceRemote.produce(dto); //STEP 3

        return result;
    }


    @Override
    @Transactional
    public void onConsumed(String svc, Object message, Runnable runnable) {

        ReliableDto dto = this.dtoConverter.convertOnConsumed(message);

        if (dto.isConsumed(svc))
            return;

        List<MessageResult> list = dto.getResultList();
        MessageResult mr = null;
        for (MessageResult messageResult : list) {
            if (messageResult.getSvc().equals(svc)) { //可以是spring.application.name
                mr = messageResult;
                break;
            }
        }

        try {
            runnable.run(); //STEP 1
        } catch (Exception e) {
            throw new ReliableExceptioin(ExceptionUtil.getMessage(e));
        }

        ConsumedReliableDto cdto = dto.consume(svc,dto.getMessage().getTcc(), mr);
        reliableServiceRemote.consume(cdto); //STEP 2

    }

    @Override
    public boolean createNext(String id, int retryMax, String nextTopic, Object nextBody,Object preMessage,String[] svcs) {

        ReliableDto dto = this.dtoConverter.convertOnConsumed(preMessage);

        String parentId = dto.getParentId();//先get parentId

        GenericObject go = new GenericObject(nextBody);
        Date date = new Date();
        ReliableMessage reliableMessage = dto.getMessage();
        reliableMessage.setId(id);
        reliableMessage.setTracingId(dto.getTracingId());
        reliableMessage.setParentId(parentId);
        reliableMessage.setTopic(nextTopic);
        reliableMessage.setBody(go);
        reliableMessage.setTcc(TCCTopic._TCC_NONE.name());
        reliableMessage.setRetryMax(retryMax);
        reliableMessage.setUnderConstruction(false);
        reliableMessage.setSvcDone("&");
        reliableMessage.setSvcList(Arrays.asList(svcs));
        reliableMessage.setSendAt(0);
        reliableMessage.setRetryCount(0L);
        reliableMessage.setCreateAt(date);
        reliableMessage.setRefreshAt(date);
        reliableMessage.setStatus(MessageStatus.NEXT.name());

        dto.setMessage(reliableMessage);
        dto.setResultList(new ArrayList<>());

        this.reliableServiceRemote.create(dto);
        return true;
    }

    @Override
    public boolean tryToConfirm(String msgId) {
        return this.reliableServiceRemote.tryToConfirm(msgId);
    }

    @Override
    public boolean cancel(String msgId) {
        return this.reliableServiceRemote.cancel(msgId);
    }


}
