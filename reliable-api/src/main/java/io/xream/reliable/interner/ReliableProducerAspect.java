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
package io.xream.reliable.interner;


import io.xream.reliable.api.ReliableProducer;
import io.xream.x7.base.exception.BusyException;
import io.xream.x7.base.util.ExceptionUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;


/**
 * @Author Sim
 */
@Aspect
public class ReliableProducerAspect {


    private final static Logger logger = LoggerFactory.getLogger(ReliableBackend.class);

    public ReliableProducerAspect() {
        logger.info("Reliable Producer Enabled");
    }

    @Autowired
    private ReliableBackend backend;

    @Pointcut("@annotation(io.xream.reliable.api.ReliableProducer))")
    public void cut() {

    }

    @Around("cut() && @annotation(reliableProducer) ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ReliableProducer reliableProducer) {

        long startTime = System.currentTimeMillis();

        Object[] args = proceedingJoinPoint.getArgs();

        Signature signature = proceedingJoinPoint.getSignature();
        String logStr = signature.getDeclaringTypeName() + "." + signature.getName();

        if (args == null || args.length == 0)
            throw new IllegalArgumentException(logStr + ", for ReliableMessage body can not be null, no args");

        Object body = null;
        if (reliableProducer.type() != Void.class) {
            for (Object arg : args) {
                if (arg.getClass() == reliableProducer.type()) {
                    body = arg;
                    break;
                }
            }
            if (body == null)
                throw new IllegalArgumentException(logStr + ", for ReliableMessage body can not be null, reliableProducer.type: " + reliableProducer.type());
        }
        if (body == null) {
            body = args[0];
        }

        MessageTraceable tracing = null;
        for (Object arg : args) {
            if (arg instanceof MessageTraceable) {
                tracing = (MessageTraceable) arg;
                break;
            }
        }

        final int retryMax = reliableProducer.useTcc() ? 0 : reliableProducer.retryMax();

        final String msgId = MessageIdGenerator.get();

        String[] svcs = reliableProducer.svcs();
        for (String svc : svcs) {
            if (svc.contains(",")) {
                throw new IllegalArgumentException(logStr + ", " + ReliableProducer.class.getName() + ", svcs: " + svcs);
            }
        }

        Object result = this.backend.produceReliably(
                reliableProducer.useTcc(),//
                msgId,//
                retryMax,//
                reliableProducer.underConstruction(),//
                reliableProducer.topic(),//
                body,//
                tracing,//
                reliableProducer.svcs(),//
                () -> {
                    try {
                        MethodSignature ms = ((MethodSignature) signature);
                        if (ms.getReturnType() == void.class) {
                            proceedingJoinPoint.proceed();
                            return null;
                        } else {
                            return proceedingJoinPoint.proceed();
                        }
                    } catch (Throwable e) {
                        if (e instanceof RuntimeException){
                            throw (RuntimeException) e;
                        }
                        throw new RuntimeException(ExceptionUtil.getMessage(e));
                    }
                }
        );

        if (reliableProducer.async() && !reliableProducer.useTcc())
            return result;

        final long intervalBaseOne = 100;//FIXME： require test
        boolean isOk = false;
        int replayMax = 3;
        long interval = intervalBaseOne;
        int replay = 0;
        while (replay < replayMax) {
            try {
                TimeUnit.MILLISECONDS.sleep(interval);
                isOk = this.backend.tryToConfirm(msgId);
                if (isOk) {
                    logger.info("handled OK time: {} ,replay = {} ,for {}", System.currentTimeMillis() - startTime, replay, logStr);
                    return result;
                }
                replay++;
            } catch (Exception e) {
                break;
            }
            interval += intervalBaseOne;
        }

        final long intervalBaseTwo = 1000;
        interval = intervalBaseTwo;
        replayMax = replay + 3;
        while (replay < replayMax) {
            try {
                TimeUnit.MILLISECONDS.sleep(interval);
                isOk = this.backend.tryToConfirm(msgId);
                if (isOk) {
                    logger.info("handled OK, time: {} ,replay = {} ,for {}", System.currentTimeMillis() - startTime, replay, logStr);
                    return result;
                }
                replay++;
            } catch (Exception e) {
                break;
            }
            interval += intervalBaseTwo;
        }

        if (retryMax == 0) {
            if (reliableProducer.useTcc()) {
                boolean flag = this.backend.cancel(msgId);
                while (!flag) {
                    // has to wait for a long time to try to cancel
                    try {
                        TimeUnit.MILLISECONDS.sleep(intervalBaseTwo);
                        isOk = this.backend.tryToConfirm(msgId);
                        if (isOk) {
                            logger.info("handled OK, time: {} ,replay = {} ,for {}", System.currentTimeMillis() - startTime, replay, logStr);
                            return result;
                        }
                        flag = this.backend.cancel(msgId);
                    }catch (Exception e){
                        flag = true;
                    }
                }
            }
            logger.info("handled FAIL, time: {} ,replay = {} ,for {}", System.currentTimeMillis() - startTime, replay, logStr);
            throw new BusyException("TIMEOUT, X TRANSACTION UN FINISHED");
        }

        return result;
    }

}
