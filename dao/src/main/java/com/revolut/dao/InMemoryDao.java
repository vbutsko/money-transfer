package com.revolut.dao;

import java.util.ArrayList;
import java.util.List;

import com.revolut.dao.model.DomainEntity;

public abstract class InMemoryDao<T extends DomainEntity> implements Dao<T> {

    protected final List<T> entities = new ArrayList<>();

}
