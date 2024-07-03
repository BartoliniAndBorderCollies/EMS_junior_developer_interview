package org.klodnicki.service.generic;

import java.util.List;

public interface BasicCrudOperations<T, K, ID> {

    T create(K object);
    T findById(ID id);
    List<T> findAll();
    T update(ID id, K object);
    void delete(ID id);
}