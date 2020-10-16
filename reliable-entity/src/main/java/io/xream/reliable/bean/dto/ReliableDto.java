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
package io.xream.reliable.bean.dto;

import io.xream.reliable.bean.entity.MessageResult;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.interner.MessageTraceable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Sim
 */
public class ReliableDto<T> implements MessageTraceable, Serializable {
    private static final long serialVersionUID = 4467713246475538455L;

    private ReliableMessage<T> message;
    private List<MessageResult> resultList = new ArrayList<>();

    public ReliableDto(){
    }

    public ReliableDto(ReliableMessage<T> message){
        this.message = message;
    }

    public ReliableDto(ReliableMessage<T> message,List<MessageResult> resultList ){
        this.message = message;
        this.resultList = resultList;
    }

    public ReliableMessage<T> getMessage() {
        return message;
    }

    public void setMessage(ReliableMessage<T> message) {
        this.message = message;
    }

    public List<MessageResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<MessageResult> resultList) {
        this.resultList = resultList;
    }

    public boolean isConsumed(String svc){
        if (message == null)
            return true;
        return message.getSvcDone().contains(svc);
    }

    public boolean isUnRegistered(String svc){
        if (message == null)
            return true;
        return ! message.getSvcList().contains(svc);
    }

    public ConsumedReliableDto consume(String consumedSvc,String tcc, MessageResult consumedMessageResult ) {
        ConsumedReliableDto dto = new ConsumedReliableDto();
        dto.setMsgId(this.message.getId());
        dto.setSvc(consumedSvc);
        dto.setTcc(tcc);
        if (consumedMessageResult != null){
            dto.setResultId(consumedMessageResult.getId());
        }
        return dto;
    }

    public ConsumedReliableDto consume(String consumedSvc) {
        ConsumedReliableDto dto = new ConsumedReliableDto();
        dto.setMsgId(this.message.getId());
        dto.setSvc(consumedSvc);
        return dto;
    }


    @Override
    public String toString() {
        return "ReliableDto{" +
                "message=" + message +
                ", resultList=" + resultList +
                '}';
    }

    @Override
    public String getTracingId() {
        return this.message.getTracingId();
    }

    @Override
    public String getParentId() {
        return this.message.getId();
    }

    @Override
    public long getTime() {
        return this.message.getSendAt();
    }
}
