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
package io.xream.reliable.bean.entity;

import io.xream.sqli.annotation.X;
import io.xream.x7.reliable.TCCTopic;
import io.xream.x7.common.bean.GenericObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author Sim
 */
public class ReliableMessage<T> implements Serializable {
    private static final long serialVersionUID = 2002563420656592953L;

    @X.Key
    private String id;
    private String tracingId;
    private String parentId;
    private int retryMax;
    private Boolean underConstruction;
    private String status;
    private List<String> svcList;
    private String tcc;
    private String svcDone;
    private String topic;
    private GenericObject<T> body;
    private Date refreshAt;
    private Date createAt;
    private Long retryCount;
    private long sendAt;


    public ReliableMessage(){}

    public ReliableMessage(String topic, Object body,List<String> svcList){
        this.topic = topic;
        if (body instanceof GenericObject){
            this.body = (GenericObject)body;
        }else {
            this.body = new GenericObject<>((T) body);
        }
        this.svcList = svcList;
    }

    public ReliableMessage(String tracingId,String topic, Object body,List<String> svcList){
        this.tracingId = tracingId;

        this.topic = topic;
        if (body instanceof GenericObject){
            this.body = (GenericObject)body;
        }else {
            this.body = new GenericObject<>((T) body);
        }
        this.svcList = svcList;
    }

    public <T> ReliableMessage(String id, int retryMax, boolean underConstruction, String tracingId, String topic, Object body, List<String> svcList) {
        this.id = id;
        this.retryMax = retryMax;
        this.underConstruction = underConstruction;
        this.tracingId = tracingId;

        this.topic = topic;
        if (body instanceof GenericObject){
            this.body = (GenericObject)body;
        }else {
            this.body = new GenericObject((T) body);
        }
        this.svcList = svcList;
    }

    public void resetTopic(TCCTopic tccTopic) {
        switch (tccTopic){
            case _TCC_TRY:
                if (!this.topic.endsWith(tccTopic.name())){
                    this.topic += tccTopic.name();
                }
                break;
            default:
                if (!this.topic.endsWith(tccTopic.name())){
                    this.topic = this.topic.replace(TCCTopic._TCC_TRY.name(),"");
                    this.topic += tccTopic.name();
                }
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTracingId() {
        return tracingId;
    }

    public void setTracingId(String tracingId) {
        this.tracingId = tracingId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    public Boolean getUnderConstruction() {
        return underConstruction;
    }

    public void setUnderConstruction(Boolean underConstruction) {
        this.underConstruction = underConstruction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTcc() {
        return tcc;
    }

    public void setTcc(String tcc) {
        this.tcc = tcc;
    }

    public List<String> getSvcList() {
        return svcList;
    }

    public void setSvcList(List<String> svcList) {
        this.svcList = svcList;
    }

    public String getSvcDone() {
        return svcDone;
    }

    public void setSvcDone(String svcDone) {
        this.svcDone = svcDone;
    }

    public Boolean getUseTcc() {
        if (this.tcc == null)
            return false;
        return ! this.tcc.equals(TCCTopic._TCC_NONE.name());
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public GenericObject<T> getBody() {
        return body;
    }

    public void setBody(GenericObject<T> body) {
        this.body = body;
    }

    public Date getRefreshAt() {
        return refreshAt;
    }

    public void setRefreshAt(Date refreshAt) {
        this.refreshAt = refreshAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Long retryCount) {
        this.retryCount = retryCount;
    }

    public long getSendAt() {
        return sendAt;
    }

    public void setSendAt(long sendAt) {
        this.sendAt = sendAt;
    }

    @Override
    public String toString() {
        return "ReliableMessage{" +
                "id='" + id + '\'' +
                ", tracingId='" + tracingId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", retryMax='" + retryMax + '\'' +
                ", underConstruction='" + underConstruction + '\'' +
                ", status='" + status + '\'' +
                ", svcList='" + svcList + '\'' +
                ", svcDone='" + svcDone + '\'' +
                ", tcc='" + tcc + '\'' +
                ", topic='" + topic + '\'' +
                ", body='" + body + '\'' +
                ", refreshAt=" + refreshAt +
                ", createAt=" + createAt +
                ", retryCount=" + retryCount +
                ", sendAt=" + sendAt +
                '}';
    }
}
