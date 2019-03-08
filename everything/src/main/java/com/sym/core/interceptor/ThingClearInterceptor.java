package com.sym.core.interceptor;

import com.sym.core.dao.FileIndexDao;
import com.sym.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor,Runnable {
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);
    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }
    public void run(){
        while(true){
            Thing thing = this.queue.poll();
            if(thing!=null){
                fileIndexDao.delete(thing);
            }
            //TODO批量删除
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
