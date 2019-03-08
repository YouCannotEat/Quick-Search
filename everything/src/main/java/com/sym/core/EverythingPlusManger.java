package com.sym.core;

import com.sym.config.EverythingPlusConfig;
import com.sym.core.dao.DataSourceFactory;
import com.sym.core.dao.FileIndexDao;
import com.sym.core.dao.FileIndexDaoImpl;
import com.sym.core.interceptor.Impl.FileIndexInterceptor;
import com.sym.core.interceptor.ThingClearInterceptor;
import com.sym.core.model.Condition;
import com.sym.core.model.HandlePath;
import com.sym.core.model.Thing;
import com.sym.core.monitor.FileWatch;
import com.sym.core.monitor.FileWatchImpl;
import com.sym.core.scan.FileScan;
import com.sym.core.scan.FileScanImpl;
import com.sym.core.search.FileSearch;
import com.sym.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 管理
 */
public class EverythingPlusManger {
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService ;
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false) ;
    private static EverythingPlusManger everythingManger;
    private FileWatch fileWatch;
    private EverythingPlusManger(){
        this.initComponent();
    }
//    //BUG
//    private void checkDatabase(){
//        String workDir = System.getProperty("user.dir");
//        String fileName = EverythingPlusConfig.getInstance().getH2IndexPath()+".mv.db";
//        File dbFile = new File(fileName);
//        if(dbFile.isFile() && !dbFile.exists()){
//            DataSourceFactory.initDatabase();
//        }
//    }
    public  void initOrResetDatabase(){
        DataSourceFactory.initDatabase();
    }
    private  void initComponent(){
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        //检查数据库
//        initOrResetDatabase();
        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
//        this.fileScan.interceptor(new FilePrintInterceptor());//调试需要，发布不需要
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);

        this.fileWatch = new FileWatchImpl(fileIndexDao);
    }

    //启动文件系统监听
    public void startFileSystemMonitor(){
        EverythingPlusConfig config = EverythingPlusConfig.getInstance();
        HandlePath handlePath = new HandlePath();
        handlePath.setIncludePath(config.getIncludePath());
        handlePath.setExcludePath(config.getExcludePath());
        this.fileWatch.monitor(handlePath);
//        System.out.println("文件系统监控启动");
        new Thread(() -> {
            System.out.println("文件系统监控启动");
            fileWatch.start();
        }).start();
    }
    //检索
    public List<Thing> search(Condition condition){
        //TODO
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            boolean flag = f.exists();
            if(!flag){
                thingClearInterceptor.apply(thing);
            }
            return flag;
        }).collect(Collectors.toList());
    }

    public static EverythingPlusManger getInstance() {
        if (everythingManger == null) {
            Class var0 = EverythingPlusManger.class;
            synchronized(EverythingPlusManger.class) {
                if (everythingManger == null) {
                    everythingManger = new EverythingPlusManger();
                }
            }
        }

        return everythingManger;
    }

    public void buildIndex(){
        initOrResetDatabase();
        Set<String> directories = EverythingPlusConfig.getInstance().getIncludePath();
        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-"+threadId.getAndIncrement());
                    return thread;
                }
            });
        }
        CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        for(String path :directories) {
            this.executorService.submit(() -> {
                EverythingPlusManger.this.fileScan.index(path);
                //当前任务完成 countdown 值-1
                countDownLatch.countDown();
            });
        }
        //阻塞直到任务完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Bulid index complete");
    }

    //启动清理线程
    public void startBackGroundClearThread(){
        if(this.backgroundClearThreadStatus.compareAndSet(false,true)){
            this.backgroundClearThread.start();
//            System.out.println("start BackgroundClear Thread");
        }else{
            System.out.println("can not repeat start BackgroundClear Thread");
        }
    }
}
