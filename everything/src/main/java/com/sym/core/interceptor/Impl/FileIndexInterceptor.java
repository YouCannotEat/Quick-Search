package com.sym.core.interceptor.Impl;

import com.sym.core.common.FileConvertThing;
import com.sym.core.dao.FileIndexDao;
import com.sym.core.interceptor.FileInterceptor;
import com.sym.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {
    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        fileIndexDao.insert(thing);
    }
}
