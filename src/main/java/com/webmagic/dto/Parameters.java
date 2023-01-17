package com.webmagic.dto;

import lombok.Data;

@Data
public class Parameters {

    //header",
    private String in;

    //Access-Token",
    private String name;

    //访问令牌",
    private String description;

    //string",
    private String type;

    //false
    private Boolean required;

    /**
     * model 关联对象  只有body的时候 才有
     * 2种数据结构
     *          "schema":{
     *               "$ref":"#/definitions/MailMsgPageQueryDto"
     *           }
     *
     *           "type":"array",
     *               "items":{
     *                   "$ref":"#/definitions/MailMsgSendDto"
     *            }
     *
     */
    private Schema schema;

}
