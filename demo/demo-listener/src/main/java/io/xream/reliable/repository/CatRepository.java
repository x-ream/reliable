package io.xream.reliable.repository;

import io.xream.reliable.bean.Cat;
import org.springframework.stereotype.Repository;
import io.xream.sqli.api.BaseRepository;

@Repository

/**
 * @Author Sim
 */
public interface CatRepository extends BaseRepository<Cat> {
}
