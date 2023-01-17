package com.webmagic.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SwaggerDto {

    private String basePath;

    /**
     * 所有的接口跟URL地址
     */
    private Map<String, Map<String,DocumentDto>> paths;

    //localhost:61675
    private String host;

    private Map<String,Definitions> definitions;

    private Info info;

    //最外层的tag
    private List<Tags> tags;
}
