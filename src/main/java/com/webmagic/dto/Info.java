package com.webmagic.dto;

import lombok.Data;

import java.util.List;

@Data
public class Info {
    //云徙科技IT
    private List<String> contact;

    //":"消息服务API",
    private String description;

    //":"http://",
    private String termsOfService;

    //":"消息服务API",
    private String title;

    //":"2.0.0"
    private String version;
}
