package com.sym.core.scan;

import com.sym.config.EverythingPlusConfig;
import com.sym.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.LinkedList;

public class FileScanImpl implements com.sym.core.scan.FileScan {
    private EverythingPlusConfig config = EverythingPlusConfig.getInstance();
    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();
    public void addFileInterceptor(FileInterceptor fileInterceptor){
        this.interceptors.add(fileInterceptor);
    }
    @Override
    public void index(String path) {
        File file = new File(path);
        if(file.isFile()){
            if(config.getExcludePath().contains(file.getParent())|| this.config.getExcludePath().contains(file.getName())) {
                return;
            } else{

            }
        }else{
            File[] files = file.listFiles();
            if(files!=null){
                for(File f:files){
                    index(f.getAbsolutePath());
                }
            }
        }
        //file ->thing 写入
        for(FileInterceptor interceptor :this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
