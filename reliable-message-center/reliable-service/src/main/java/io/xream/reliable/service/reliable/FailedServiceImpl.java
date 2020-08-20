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

import io.xream.reliable.api.reliable.FailedService;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.repository.reliable.ReliableMessageRepository;
import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FailedServiceImpl implements FailedService {

    @Autowired
    private ReliableMessageRepository reliableMessageRepository;

    @Override
    public boolean refresh(RefreshCondition<ReliableMessage> condition) {
        return this.reliableMessageRepository.refresh(condition);
    }

    @Override
    public boolean refreshUnSafe(RefreshCondition<ReliableMessage> condition) {
        return this.reliableMessageRepository.refreshUnSafe(condition);
    }

    @Override
    public List<Map<String, Object>> listByResultMap(Criteria.ResultMappedCriteria resultMappedCriteria) {
        return this.reliableMessageRepository.list(resultMappedCriteria);
    }
}
