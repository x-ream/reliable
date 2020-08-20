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

import java.io.Serializable;
import java.util.Date;

public class MessageResult<T> implements Serializable {
    private static final long serialVersionUID = 4901898223573975818L;

    @X.Key
    private String id;
    private String msgId;
    private String status;
    private String svc;
    private Date createAt;
    private Date refreshAt;

    public MessageResult(){
    }
    public MessageResult(String svc){
        this.svc = svc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSvc() {
        return svc;
    }

    public void setSvc(String svc) {
        this.svc = svc;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getRefreshAt() {
        return refreshAt;
    }

    public void setRefreshAt(Date refreshAt) {
        this.refreshAt = refreshAt;
    }

    @Override
    public String toString() {
        return "MessageResult{" +
                "id='" + id + '\'' +
                ", msgId='" + msgId + '\'' +
                ", svc='" + svc + '\'' +
                ", status='" + status + '\'' +
                ", createAt=" + createAt +
                ", refreshAt=" + refreshAt +
                '}';
    }
}
