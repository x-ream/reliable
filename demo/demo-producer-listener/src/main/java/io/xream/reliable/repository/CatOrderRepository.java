package io.xream.reliable.repository;

import io.xream.reliable.bean.CatOrder;
import org.springframework.stereotype.Repository;
import io.xream.sqli.api.BaseRepository;

@Repository
public interface CatOrderRepository extends BaseRepository<CatOrder> {
}
