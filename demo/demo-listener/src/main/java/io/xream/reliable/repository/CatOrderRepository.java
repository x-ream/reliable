package io.xream.reliable.repository;

import io.xream.reliable.bean.CatOrder;
import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatOrderRepository extends BaseRepository<CatOrder> {
}
