package com.sym.core.search.impl;

import com.sym.core.dao.FileIndexDao;
import com.sym.core.model.Condition;
import com.sym.core.model.Thing;
import com.sym.core.search.FileSearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;
    public FileSearchImpl(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
    }
    @Override
    public List<Thing> search(Condition condition) {
        if (condition==null)
            return new ArrayList<>();
        //数据库的处理逻辑
        return this.fileIndexDao.search(condition);
    }

}
