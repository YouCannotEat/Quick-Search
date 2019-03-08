package com.sym.core.dao;

import com.sym.core.model.Condition;
import com.sym.core.model.Thing;

import java.util.List;
import java.util.Set;

public interface FileIndexDao {
    void insert(Thing thing);
    List<Thing> search(Condition condition);
    void delete(Thing thing);
}
