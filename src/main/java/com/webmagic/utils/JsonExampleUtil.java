package com.webmagic.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webmagic.constants.ExcelConst;
import com.webmagic.dto.Definitions;
import com.webmagic.dto.DocumentDto;
import com.webmagic.dto.Parameters;
import com.webmagic.dto.RespPropertiesDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.regexp.RE;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonExampleUtil {



    /**
     * JSON 输出序列化
     * @param definitions Json的
     * @param dto
     * @return
     */
    public static String requestJsonSerialize(Map<String, Definitions> definitions, DocumentDto dto){
        List<Object> showData=new ArrayList<>();
        StringBuffer stringBuffer=new StringBuffer();
        //4种类型 空 query  dto  List<dto>
        List<Parameters> collect = dto.getParameters().stream().filter(i -> ExcelConst.DATA_OBJECT.contains(i.getIn()) && StringUtils.isNotBlank(i.getName())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)){
            long isBody = collect.stream().filter(i -> i.getIn().equals(ExcelConst.BODY)).count();
            if (isBody == 1){
                collect.stream().forEach(i->{
                    if (StringUtils.isNotBlank(i.getSchema().getType()) && ExcelConst.ARRAY.equals(i.getSchema().getType())){
                        showData.add(outObjectJsonToString(definitions, getRef(i.getSchema().getItems().get(0).getRef())));
                    }else{
                        stringBuffer.append(outObjectJsonToString(definitions, getRef(i.getSchema().getRef())));
                        return;
                    }
                });
            }else {
                Map<String,Object> map=new HashMap<>();
                collect.stream().filter(i->StringUtils.isNotBlank(i.getName())).forEach(i->{
                    map.put(i.getName(),outObjectJsonToString(i.getType()));
                });
                stringBuffer.append(JSONObject.toJSONString(map));
            }

            //用于区分是数组返回还是对象返回
            if (StringUtils.isNotBlank(stringBuffer)){
                return stringBuffer.toString();
            }
            return showData.toString();
        }
        return showData.toString();
    }

    /**
     * 解析链接Dto对象
     * @param refLink
     * @return
     */
    public static String getRef(String refLink){
        if (StringUtils.isBlank(refLink)){
            return null;
        }
        return refLink.replace("#/definitions/","");
    }

    /**
     * 获取数组类型
     * @param objecyType
     * @return
     */
    public static String getArrayType(String objecyType) {
        return "List<" + objecyType + ">";
    }


    /**
     * 封装返回对象
     * @param definitions
     * @param refLink
     * @return
     */
    public static String outObjectJsonToString(Map<String, Definitions> definitions,String refLink){
        Map<String,Object> map=new HashMap<>();
//        System.out.println(refLink);
        Definitions childNode = definitions.get(refLink);
        if (null !=childNode && MapUtil.isNotEmpty(childNode.getProperties())){
            childNode.getProperties().forEach((k,v)->{
                if (StringUtils.isNotBlank(v.getType())){
                    switch (v.getType()){
                        case "string":
//                            map.put(k,RandomUtil.randomString(5));
                            if (StringUtils.isNotBlank(v.getFormat()) && "date-time".equals(v.getFormat())){
                                map.put(k,DateUtil.now());
                            }else{
                                map.put(k,"sting");
                            }
                            break;
                        case "integer":
//                            map.put(k,RandomUtil.randomInt(99));
                            map.put(k,0);
                            break;
                        case "object":
//                            map.put(k,RandomUtil.randomString(5));
                            map.put(k,"string");
                            break;
                        case "array":
                            if (StringUtils.isNotBlank(v.getItems().getRef())){
//                                List<String> all = ReUtil.findAll("definitions(.*)(type)", JSONObject.toJSONString(v), 0, new ArrayList<>());
                                map.put(k,getNodeJson(v.getItems().getRef(), definitions,refLink));
                            }else{
                                //数组对象
                                switch (v.getItems().getType()){
                                    case "string":
//                                        map.put(k,"["+RandomUtil.randomString(5)+"]");
                                        map.put(k,"[string]");
                                        break;
                                    case "integer":
//                                        map.put(k,"["+RandomUtil.randomInt(99)+"]");
                                        map.put(k,"[0]");
                                        break;
                                    case "object":
//                                        map.put(k,"["+RandomUtil.randomString(5)+"]");
                                        map.put(k,"[string]");
                                        break;
                                    case "boolean":
//                                        map.put(k,"["+RandomUtil.randomBoolean()+"]");
                                        map.put(k,RandomUtil.randomBoolean());
                                        break;
                                }
                            }
                            break;
                        case "boolean":
                            map.put(k,RandomUtil.randomBoolean());
                            break;
                    }
                } else {
                    if (StringUtils.isNotBlank(v.getRef())){
                        String s = getNodeJson(v.getRef(), definitions,refLink);
                        map.put(k,s);
                    }else{
                        System.out.println("特殊字符"+JSONObject.toJSONString(v));
                    }
                }
            });
            //\n\t\"
            return JSON.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
//             return JSON.toJSONString(map);
        }
        return "";
    }

    /**
     * 获取对象子节点数据
     * @param v
     * @param definitions
     * @param refLink
     * @return
     */
    private static String getNodeJson(String v, Map<String, Definitions> definitions,String refLink) {
        String link = JsonExampleUtil.getRef(v);
        if (!link.equals(refLink)){
            String s = outObjectJsonToString(definitions, link);
            return s;
        }
        return "";

    }

    /**
     * 封装单个对象 query 的时候 对象在header
     * @param objectType
     * @return
     */
    public static Object outObjectJsonToString(String objectType) {
        switch (objectType) {
            case "string":
                return RandomUtil.randomString(5);
            case "integer":
                return RandomUtil.randomInt(99);
            case "object":
                return RandomUtil.randomString(5);
            case "boolean":
                return RandomUtil.randomBoolean();
            default:
                return RandomUtil.randomString(5);
        }
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex 匹配的正则
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, String content, int groupIndex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, String content, int groupIndex) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    private static String toJson(String json){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String formatStr = gson.toJson(jsonObject);
        System.out.println("==================================");
        System.out.println(formatStr);
        return  formatStr;
    }

}
