package io.xream.reliable.repository;

import io.xream.reliable.bean.Cat;
import io.xream.sqli.api.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository

/**
 * @Author Sim
 */
public interface CatRepository extends BaseRepository<Cat> {
}
