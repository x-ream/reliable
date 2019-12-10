package io.xream.reliable.api.reliable;

import io.xream.reliable.bean.entity.ReliableMessage;
import x7.core.bean.Criteria;
import x7.core.bean.condition.RefreshCondition;

import java.util.List;
import java.util.Map;

public interface FailedService {

    boolean refresh(RefreshCondition<ReliableMessage> condition);
    boolean refreshUnSafe(RefreshCondition<ReliableMessage> condition);

    List<Map<String, Object>> listByResultMap(Criteria.ResultMappedCriteria resultMappedCriteria);
}
