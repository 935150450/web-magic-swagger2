package com.webmagic.dto;

import lombok.Data;

import java.util.List;

/**
 * model 关联对象  只有body的时候 才有
 */
@Data
public class Schema {

    //"type":"array"
    private String type;

    /**
     * "items":{
     * 			"type":"integer",
     * 			"format":"int64"
     *  }
     *  "items":{
     * 			"$ref":"#/definitions/MailMsgSendDto"
     *   }
     */
    private List<item> items;

    private String ref;



    @Data
    public class item{
        //"$ref":"#/definitions/MailMsgSendDto"
        private String ref;
        //":"integer",
        private String type;
        //":"int64"
        private String format;
    }
}
