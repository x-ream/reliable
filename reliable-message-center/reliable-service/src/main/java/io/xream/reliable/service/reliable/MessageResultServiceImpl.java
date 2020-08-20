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

import io.xream.reliable.api.reliable.MessageResultService;
import io.xream.reliable.bean.entity.MessageResult;
import io.xream.reliable.repository.reliable.MessageResultRepository;
import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.xream.sqli.repository.api.ManuRepository;

import java.util.List;

@Service
public class MessageResultServiceImpl implements MessageResultService {

    @Autowired
    private MessageResultRepository repository;

    public boolean create(MessageResult result) {
        this.repository.create(result);
        return true;
    }

    public boolean refresh(RefreshCondition<MessageResult> condition) {
        return this.repository.refresh(condition);
    }

    public boolean remove(String id) {
        return this.repository.remove(id);
    }

    @Override
    public boolean removeByMessageId(String id) {
        String sql = "delete from messageResult where msgId = " + id;
        return ManuRepository.execute(new MessageResult(),sql );
    }

    @Override
    public List<MessageResult> listByCriteria(Criteria criteria) {
        return this.repository.list(criteria);
    }


}
