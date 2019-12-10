package io.xream.reliable.service.reliable;

import io.xream.reliable.api.reliable.FailedService;
import io.xream.reliable.bean.entity.ReliableMessage;
import io.xream.reliable.repository.reliable.ReliableMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x7.core.bean.Criteria;
import x7.core.bean.condition.RefreshCondition;

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
