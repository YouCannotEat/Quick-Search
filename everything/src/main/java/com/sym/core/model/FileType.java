package com.sym.core.model;

import javax.sound.midi.Soundbank;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件类型 枚举
 */
public enum FileType {
    IMG(new String[]{"png", "jpeg", "jpe", "gif"}),
    DOC(new String[]{"doc", "docx", "pdf", "ppt", "txt","pptx"}),
    BIN(new String[]{"exe", "sh", "jar", "msi"}),
    ARCHIVDE("zip","rar"),
    OTHER(new String[]{"*"});
    private Set<String> extend = new HashSet<>();
    FileType(String ... extend){
        this.extend.addAll(Arrays.asList(extend));
    }
    public static FileType lookup(String extend){
        for(FileType fileType:FileType.values()){
            if(fileType.extend.contains(extend)){
                return fileType;
            }
        }
        return OTHER;
    }
    public static FileType lookBYName(String name){
        //根据类型名获取文件类型对象
        for(FileType fileType:FileType.values()){
            if(fileType.name().equals(name)){
                return fileType;
            }
        }
        return OTHER;
    }

}
