package com.webmagic.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * request 和 response 公用一个对象
 */
@Data
public class Definitions {

    //接口描述备注:邮件发送Dto对象
    private String description;

    //对象类型 object
    private String type;

    //RestResponse«Void»        接口链接Dto
    private String title;

    //mailSendTo
    private List<String> required;

    //返回类型
    private Map<String,RespPropertiesDto> properties;
}
