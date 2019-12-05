package io.xream.reliable.repository;

import io.xream.reliable.bean.Cat;
import org.springframework.stereotype.Repository;
import x7.repository.BaseRepository;

@Repository
public interface CatRepository extends BaseRepository<Cat> {
}
