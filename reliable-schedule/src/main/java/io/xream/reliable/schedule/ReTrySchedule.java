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
package io.xream.reliable.schedule;

import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.codetemplate.ScheduleTemplate;
import io.xream.reliable.remote.ScheduledReliableServiceRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
public class ReTrySchedule {

    private final static Logger logger = LoggerFactory.getLogger(ReTrySchedule.class);

    public ReTrySchedule(){
        logger.info("ReProduce Schedule Started");
    }

    @Autowired
    private ScheduledReliableServiceRemote remote;

    @Autowired
    private ScheduleTemplate scheduleTemplate;

    @Scheduled(cron = "0/7 * * * * ?")
    public void reProduce(){

        scheduleTemplate.schedule(ReTrySchedule.class, () -> {

            boolean flag = false;
            List<ReliableMessage> list = remote.listForRetry();

            flag = true;

            for (ReliableMessage message : list) {
                flag &= remote.retry(message);
            }

            return flag;
        });

    }

}
