package com.sym.core.interceptor.Impl;

import com.sym.core.interceptor.FileInterceptor;

import java.io.File;

public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file);
    }
}
