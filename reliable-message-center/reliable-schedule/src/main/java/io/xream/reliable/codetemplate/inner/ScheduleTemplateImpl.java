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
package io.xream.reliable.codetemplate.inner;


import io.xream.reliable.codetemplate.ScheduleTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.xream.x7.common.util.ExceptionUtil;

import java.util.Date;
import java.util.concurrent.Callable;


public class ScheduleTemplateImpl implements ScheduleTemplate {

    public boolean schedule(Class scheduleClazz, Callable<Boolean> callable) {

        Logger logger = LoggerFactory.getLogger(scheduleClazz);

        String loggingName = scheduleClazz.getSimpleName();

        boolean flag = false;
        long startTime = System.currentTimeMillis();
        String taskId = loggingName + "_" + startTime;

        logger.info("Executing {}, At: {}, id: {}", loggingName, new Date(), taskId);

        try {
            flag = callable.call();
        }catch (Exception e) {
            logger.info("Exception Occured: {}" ,ExceptionUtil.getMessage(e));
        }

        long endTime = System.currentTimeMillis();

        String result = flag == true?"OK," :  "FAILED,";
        logger.info("Executing {}, {}, Cost: {}ms id: {}",
                loggingName, result,
                (endTime - startTime),
                taskId);

        return flag;
    }
}
