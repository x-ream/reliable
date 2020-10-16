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
package io.xream.reliable.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1. retryMax=0,useTcc=false,async=false, mean: simple prepare,
 *      producer local tx create preparing status record or log record
 *      listener local tx create preparing status record or log record
 * 2. useTcc=true, anyway, framework will set retryMax = 0
 * 3. retryMax=3, final consistent; if underConstruction, will save the message
 *
 * usage:
 * 1. create orderBean: (useTcc=true) or (retryMax=0,useTcc=false,async=false)
 * 2. pay:  preparing(retryMax=0,useTcc=false,async=false), paid callback(retryMax=3,async=true)
 * 3. logistics warehousing: (retryMax=3), and client ui vision compensation
 *
 * @Author Sim
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReliableProducer {

    boolean useTcc() default false;

    String topic() default "";

    /**
     * define which arg is the message body, while has multi args
     */
    Class<?> type() default Void.class;

    String[] svcs() default {};

    int retryMax() default 0;

    /**
     * to avoid long tx waiting, <br>
     * if async == false, not suggest to write data in db of the method  annotated by ReliableProducer <br>
     */
    boolean async() default false;

    boolean underConstruction() default false;
}
