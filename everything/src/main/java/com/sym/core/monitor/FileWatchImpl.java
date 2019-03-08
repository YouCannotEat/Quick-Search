package com.sym.core.monitor;

import com.sym.core.common.FileConvertThing;
import com.sym.core.dao.FileIndexDao;
import com.sym.core.model.HandlePath;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

public class FileWatchImpl implements FileAlterationListener,FileWatch {
    private FileIndexDao fileIndexDao;
    private FileAlterationMonitor monitor;
    public FileWatchImpl(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10);
    }
    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        fileAlterationObserver.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File file) {
        System.out.println("onDirectoryCreate"+file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("FileCreate"+file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("FileDelete"+file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
       fileAlterationObserver.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        //监控includepath是一组集合
        for(String path:handlePath.getIncludePath()){
            FileAlterationObserver observer = new FileAlterationObserver(path, pathname -> {
                String currentPath = pathname.getAbsolutePath();
                for(String excludePath:handlePath.getExcludePath()){
                    if(excludePath.startsWith(currentPath)){
                        return false;
                    }
                }
                return true;
            });
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }
}
