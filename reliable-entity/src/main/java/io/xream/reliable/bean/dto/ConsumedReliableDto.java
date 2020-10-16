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

import io.xream.reliable.TCCTopic;

import java.io.Serializable;

/**
 * @Author Sim
 */
public class ConsumedReliableDto implements Serializable {
    private static final long serialVersionUID = 4467713246475543155L;

    private String msgId;
    private String svc;
    private String resultId;
    private String tcc;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSvc() {
        return svc;
    }

    public void setSvc(String svc) {
        this.svc = svc;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public Boolean getUseTcc() {
        return ! tcc.equals(TCCTopic._TCC_NONE);
    }

    public String getTcc() {
        return tcc;
    }

    public void setTcc(String tcc) {
        this.tcc = tcc;
    }

    @Override
    public String toString() {
        return "ConsumedReliableDto{" +
                "msgId='" + msgId + '\'' +
                ", svc='" + svc + '\'' +
                ", resultId='" + resultId + '\'' +
                ", tcc='" + tcc + '\'' +
                '}';
    }
}
