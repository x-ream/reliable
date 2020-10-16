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

/**
 * @author Sim 8966188@qq.com
 * @apiNote to use @EnableReliabilityManagement, @ReliableProducer and @ReliableOnConsumed, <br>
 *     reliable center's DTO has to implements MessageTraceable <br>
 *     sql update in order, need getTime() <br>
 */
public interface MessageTraceable {

    String getTracingId();

    /**
     *
     * full tx: after finishing all write, then produce next message(if necessary) <br>
     * at first persist the next message, after persist all biz data, then find the message <br>
     * by parentId,and produce it <br>
     */
    String getParentId();

    /**
     * for server, scheduling retry, java code like:  <br>
     *     if ( message.getTime() < now-interval) <br>
     * for client, as condition of sql update for 2pc business, sql code like: <br>
     *    update inventory set refreshAt = ${message.time} where id = 1 and refreshAt < ${message.time} <br>
     */
    long getTime();
}
