package io.xream.reliable.repository;

import io.xream.reliable.bean.Cat;
import org.springframework.stereotype.Repository;
import io.xream.x7.repository.BaseRepository;

@Repository
public interface CatRepository extends BaseRepository<Cat> {
}
