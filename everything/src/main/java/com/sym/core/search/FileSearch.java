package com.sym.core.search;

import com.sym.core.model.Condition;
import com.sym.core.model.Thing;

import java.util.List;
import java.util.Set;

public interface FileSearch {
    List<Thing> search(Condition condition);
}
