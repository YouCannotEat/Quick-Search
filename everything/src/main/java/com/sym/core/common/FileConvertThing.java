package com.sym.core.common;

import com.sym.core.model.FileType;
import com.sym.core.model.Thing;

import java.io.File;

public final class FileConvertThing {
    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }
    private static int computerFileDepth(File file){
        int depth;
        String[] segments = file.getAbsolutePath().split("\\\\");
        depth = segments.length;
        return depth;
    }
    private static FileType computerFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if(index!=-1 && index <fileName.length() -1 ){
             String extend = file.getName().substring(index+1);
            return FileType.lookup(extend);
        }else {
            return FileType.OTHER;
        }
    }
}
