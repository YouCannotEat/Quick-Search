package com.sym.core.model;

import lombok.Data;

@Data
public class Condition {
    private String name;
    private String fileType;
    private Integer limit;
    private Boolean orderByAsc = true;

    //检索结果文件信息depth排序规则：默认是true-asc false-desc
}
