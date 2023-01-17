package com.webmagic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    //接口名称 描述
    private String summary;
    //
    private Boolean deprecated;

    // swagger 详情裂隙 Response content type
    private List<String> produces;

    //接口名称 描述  :获取微信公众号accessToken
    private String description;

    //接口唯一编号 规则URL+请求类型:
    private String operationId;

    //响应code
    private Map<Integer,Parameters> responses;

    //header 头部鉴权内容
    private List<Parameters> parameters;

    //最外层的tags
    private List<String> tags;

    //请求协议  application/json
    private List<String> consumes;
}
