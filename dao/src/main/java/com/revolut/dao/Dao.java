package com.revolut.dao;

import java.util.List;
import java.util.Optional;

import com.revolut.dao.model.DomainEntity;

public interface Dao<T extends DomainEntity> {

    Optional<T> getEntity(String uuid);

    T save(T entity);

    Optional<T> delete(String uuid);

    List<T> deleteAll();

}
