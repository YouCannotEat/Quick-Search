package com.sym.core.model;

import lombok.Data;

/**
 * 文件属性索引之后的记录
 */
@Data
public class Thing {
    private String name;//文件名称
    private String path;//路径
    private Integer depth;//深度
    private FileType fileType;//文件类型

}

