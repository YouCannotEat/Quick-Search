package com.sym.config;
/**
 * 配置
 */

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
@Getter
@ToString
public  class EverythingPlusConfig {
    private static volatile EverythingPlusConfig config;
    private Set<String> includePath = new HashSet<>();//建立索引的路径
    private Set<String> excludePath = new HashSet<>();//排除索引的路径
    //H2数据库文件路径
     private String h2IndexPath = System.getProperty("user.dir")+ File.separator+"everything_plus.sql";
     //检索最大的返回值数量
    @Setter
     private Integer maxReturn = 40;
     //默认深度排序规则，默认升序 true = asc
    @Setter
     private Boolean deptOrderAsc = true;
    //TODO  可配置的参数在这里体现
    private EverythingPlusConfig(){
    }

    private static void initDefaultPathsConfig(){
        //初始化默认的配置
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //2.遍历目录
        Iterable<Path> iterable =  fileSystem.getRootDirectories();
        iterable.forEach(new Consumer<Path>() {
            public void accept(Path path) {
                EverythingPlusConfig.config.getIncludePath().add(path.toString());
            }
        });
        //排除目录
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows")){
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files(x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");
        }else{
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }
    public static EverythingPlusConfig getInstance(){
        if(config == null){
            synchronized (EverythingPlusConfig.class){
                if(config==null){
                    config = new EverythingPlusConfig();
                    //遍历的目录 排除的目录
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

}
