package com.webmagic.dto;

import lombok.Data;

@Data
public class RespPropertiesDto {

    //string "array",
    private String type;

    //主题，非必填，为空时使用模板标题
    private String description;
    /*                  响应报文内容             */
    //可能存在ref
    private Items items;
    //可能存在ref
    private String ref;

    @Data
    public class Items{
        //"type": "string"
        private String type;
        //"int32"
        private String format;
        // "ref": "#/definitions/SortDto"
        private String ref;

    }

}

