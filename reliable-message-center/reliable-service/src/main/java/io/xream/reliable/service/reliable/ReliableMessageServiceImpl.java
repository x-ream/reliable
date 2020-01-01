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
package io.xream.reliable.service.reliable;

import io.xream.reliable.api.reliable.ReliableMessageService;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.repository.reliable.ReliableMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.xream.x7.common.bean.Criteria;
import io.xream.x7.common.bean.condition.RefreshCondition;

import java.util.List;
import java.util.Map;

@Service
public class ReliableMessageServiceImpl implements ReliableMessageService {

    @Autowired
    private ReliableMessageRepository repository;
    @Override
    public boolean create(ReliableMessage message) {
        this.repository.create(message);
        return true;
    }

    @Override
    public boolean refresh(RefreshCondition<ReliableMessage> refreshCondition) {
        return this.repository.refresh(refreshCondition);
    }

    @Override
    public boolean remove(String id) {
        return this.repository.remove(id);
    }

    @Override
    public List<ReliableMessage> listByCriteria(Criteria criteria) {
        return this.repository.list(criteria);
    }

    @Override
    public List<Map<String, Object>> listByResultMap(Criteria.ResultMappedCriteria resultMappedCriteria) {
        return this.repository.list(resultMappedCriteria);
    }

    @Override
    public ReliableMessage get(String msgId) {
        return this.repository.get(msgId);
    }
}
