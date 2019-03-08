package com.sym.core.scan;

import com.sym.core.interceptor.FileInterceptor;
import com.sym.core.model.Thing;

public interface FileScan {
    void index(String path);
    void interceptor(FileInterceptor interceptor);
}
