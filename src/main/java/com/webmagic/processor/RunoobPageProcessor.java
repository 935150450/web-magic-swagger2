package com.webmagic.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.webmagic.component.HtmlParserComponent;
import com.webmagic.dto.*;
import com.webmagic.utils.HutoolExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Cheney
 * @title: GithubRepoPageProcessor
 * @projectName JR-WebMagic
 * @description: 爬虫处理器
 * @date 2019/6/1315:08
 */
public class RunoobPageProcessor implements PageProcessor {
    //ref解析对象
    private final List<String> reqestType=Arrays.asList("body","query");
    //url 和 接口编号
    private static Map<String,String> urlMap=new HashMap<>();

    private HtmlParserComponent htmlParserComponent;
    //列
    private int column = 6;

    public RunoobPageProcessor(HtmlParserComponent htmlParserComponent) {
        this.htmlParserComponent = htmlParserComponent;
    }

    /**
     *  抓取网站的相关配置，包括编码、重试次数、抓取间隔、超时时间、请求消息头、UA信息等
     */
    private Site site= Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(3000).addHeader("Accept-Encoding", "/")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.59 Safari/537.36");

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    //此处为处理函数
    public void process(Page page) {
        Html html = page.getHtml();
        String pattern = "<body[^>]*>([\\s\\S]*)<\\/body>";

        Pattern p_body = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m_body = p_body.matcher(html.get());
        if (m_body.find()){
            //获取swagger json
            String replace = m_body.group().replace("<body>", "").replace("</body>", "").replace("$ref","ref");
            SwaggerDto swaggerDto=JSONObject.parseObject(replace, SwaggerDto.class);
            //拿到完整json 可以开始准备导出swagger
            creaetExcel(swaggerDto);
        }
        System.out.println("文件下载完成");
    }


    private void creaetExcel(SwaggerDto swaggerDto){
        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("d:/writeBeanTest"+System.currentTimeMillis()+".xlsx");
        Font font = HutoolExcelUtil.createFont(writer, false, false, "宋体", 11);
        writer.setStyleSet(HutoolExcelUtil.setBaseGlobalStyle(writer, font));
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(16, "SCRM与GRT接口交互梳理一览");
        //自定义标题别名
        getSheel1(writer,swaggerDto);
        //设置其他Sheet页
        setOtherSheet(writer,swaggerDto);
        writer.close();
    }


    /**
     *
     */

    private static void getSheel1(ExcelWriter writer,SwaggerDto swaggerDto){
        //自定义标题别名
        writer.getWorkbook().setSheetName(0,"接口一览");
        //设置标题
        writer.addHeaderAlias("source", "源系统");
        writer.addHeaderAlias("target", "目标系统");
        writer.addHeaderAlias("type", "来源到目标传输逻辑(来源/目标，推/拉，主动/被动)");
        writer.addHeaderAlias("interfaceNo", "接口编号");
        writer.addHeaderAlias("name", "接口名称");
        writer.addHeaderAlias("requestType", "请求类型");
        writer.addHeaderAlias("url", "url");
        writer.addHeaderAlias("description", "接口使用场景描述");
        writer.addHeaderAlias("technologyType", "接口实现技术方式");
        writer.addHeaderAlias("dataVolume", "平均数据量");
        writer.addHeaderAlias("responseTime", "平均响应时间");
        writer.addHeaderAlias("sync", "同步/异步");
        writer.addHeaderAlias("frequency", "实时/定时执行(及频次)");
        writer.addHeaderAlias("httpType", "协议");
        writer.addHeaderAlias("dataType", "数据格式");
        writer.addHeaderAlias("security", "安全认证方式");
        writer.addHeaderAlias("importance", "重要性(高、中、低)");
        writer.addHeaderAlias("remarks", "备注");
        //生成URL 个接口编号
        createInterfaceNo(swaggerDto.getPaths());
        //输出接口接口一览
        List<Map<String, String>> list = setData(swaggerDto);
        // 默认的，未添加alias的属性也会写出，如果想只写出加了别名的字段，可以调用此方法排除之
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        writer.getStyleSet().setAlign(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
        Workbook workbook = writer.getWorkbook();
        Sheet sheetAt = workbook.getSheetAt(0);
        //自动宽度
        HutoolExcelUtil.setSizeColumn(sheetAt,sheetAt.getRow(1).getPhysicalNumberOfCells());
//        Row row = sheetAt.getRow(1);
//        for (int i = 0; i <row.getLastCellNum();i++) {
//            sheetAt.setColumnWidth(i,  (int)((row.getCell(i).getStringCellValue().getBytes().length + 2) * 200));   // 在EXCEL文档中实际列宽为100
//        }
    }


    private static List<Map<String,String>> setData(SwaggerDto swaggerDto){
        List<Map<String,String>> list=new ArrayList<>();
        swaggerDto.getPaths().forEach((k,v)->{
            Map<String,String> map=new HashMap<>();
            map.put("source", "BCG");
            map.put("target", "目标系统");
            map.put("type", "主动推");
            map.put("interfaceNo", urlMap.get(k));
            map.put("name", "接口名称");
            v.forEach((k1,k2)->{
                map.put("name", k2.getSummary());
                map.put("requestType", k1);
                map.put("description", k2.getDescription());
            });
            map.put("url", k);
            map.put("technologyType", "");
            map.put("dataVolume", "");
            map.put("responseTime", "");
            map.put("sync", "");
            map.put("frequency", "实时");
            map.put("httpType", "http/https");
            map.put("dataType", "json");
            map.put("security", "账号密码");
            map.put("importance", "高");
            map.put("remarks", "");
            list.add(map);
        });
        return list;
    }

    /**
     * 设置其他sheet页
     */
    private void setOtherSheet(ExcelWriter writer,SwaggerDto swaggerDto){
        Map<String, Map<Object,Object>> interfaceInfo = getInterfaceInfo();
        swaggerDto.getPaths().forEach((k,v)->{
            //创建新的单元表格
            if (!v.values().isEmpty()){
                DocumentDto dto=v.values().stream().findAny().get();
                //待处理对象，为了完整显示数据结构
                List<String> pendingData=new ArrayList<>();
                //创建新的sheel   sheel 不能有 / 字符 ，需要替换
                writer.setSheet(urlMap.get(k)+"-"+dto.getSummary().replace("/","&"));
                // 合并单元格后的标题行，使用默认标题样式
                Sheet sheet = writer.getSheet();

                //title
                CellStyle cellStyle = HutoolExcelUtil.buildCellStyle(writer.getWorkbook());
                //字体加粗
                CellStyle titleFont = HutoolExcelUtil.createTitleFont(writer.getWorkbook());
                //天蓝色
                CellStyle paleBlue = HutoolExcelUtil.buildInterfaceInfoCellStyle(writer.getWorkbook(), IndexedColors.PALE_BLUE.getIndex());
                //灰色
                CellStyle grey25Percent = HutoolExcelUtil.buildInterfaceInfoCellStyle(writer.getWorkbook(), IndexedColors.GREY_25_PERCENT.getIndex());
                //上下左右框
                CellStyle frame = HutoolExcelUtil.buildFrameCellStyle(writer.getWorkbook());


                //控制当前行数
                AtomicInteger rowi= new AtomicInteger(0);
                //创建：服务基本信息、服务类型信息、服务地址信息
                interfaceInfo.forEach((k1,k2)->{
                    setTitleCallValue(sheet, rowi, k1,titleFont);
                    rowi.getAndIncrement();

                    k2.forEach((v1,v2)->{
                        setCallValue(sheet, rowi,k, (String) v1,(String)v2,v.keySet(),paleBlue,grey25Percent);
                        rowi.getAndIncrement();
                    });
                });
                //设置全局样式
                //备注
                setRemarks(sheet,rowi,"*业务描述",titleFont);
                setRemarks(sheet,rowi,"");
                setRemarks(sheet,rowi,"");
                /*                    设置头部信息                          */
                setRemarks(sheet,rowi,"HTTP Header",titleFont);
                setTableHeadertitle(sheet,rowi,paleBlue);
                setTableHeaderData(sheet,rowi,dto.getParameters(),frame);
                if (sheet.getSheetName().equals("YX30-线索调用GRT同步渠道来源信息 测试联调,后面要删")){
                    System.out.println("123");
                }
                /*                    请求报文开始                          */
                setRemarks(sheet,rowi,"*请求报文",titleFont);
                setRemarks(sheet,rowi,"");//请求的JSON 暂时不写
                //请求参数说明
                setRemarks(sheet,rowi,"*请求内容字段说明");
                setTableHeadertitle(sheet,rowi,paleBlue);
                setTableRequestData(sheet,rowi,dto.getParameters(),swaggerDto,pendingData,frame);
                /*                    响应内容部分                          */
                setRemarks(sheet,rowi,"*响应报文",titleFont);
                setRemarks(sheet,rowi,"");
                setRemarks(sheet,rowi,"*响应内容字段说明:",titleFont);
                setTableHeadertitle(sheet,rowi,paleBlue);
                setTableResponseData(sheet,rowi,dto,swaggerDto,pendingData,frame);


                /*                    Java对象部分                          */
                if (CollectionUtils.isNotEmpty(pendingData)){
                    //待处理对象，为了完整显示数据结构
                    rowi.getAndIncrement();
                    rowi.getAndIncrement();
                    rowi.getAndIncrement();
                    pendingData.stream().forEach(i->{
                        rowi.getAndIncrement();
                        setRemarks(sheet,rowi,i,cellStyle);
                        setTableHeadertitle(sheet,rowi,paleBlue);
                        setPojoData(sheet,rowi,swaggerDto,i,frame);
                        rowi.getAndIncrement();
                    });
                }

                Row row = sheet.getRow(19);
                for (int i = 0; i <row.getLastCellNum();i++) {
                    sheet.setColumnWidth(i,  30 * 256);   // 在EXCEL文档中实际列宽为100
                }
            }
        });

    }

    private void setPojoData(Sheet sheet,AtomicInteger rowi,SwaggerDto swaggerDto,String refLink,CellStyle cellStyle){
        AtomicInteger k= new AtomicInteger(1);
        parseData(sheet, rowi, swaggerDto, k, refLink,cellStyle);
    }

    /**
     * 标题单元格
     * @param sheet
     * @param rowi
     * @param j
     */
    private void setTitleCallValue(Sheet sheet, AtomicInteger rowi, String j,CellStyle titleFont) {
        Row row = sheet.getRow(rowi.get());
        if (null ==row){
            row= sheet.createRow(rowi.get());
            row.createCell(0).setCellValue(j);
            row.getCell(0).setCellStyle(titleFont);
            sheet.addMergedRegion(new CellRangeAddress(rowi.get(),rowi.get(),0,5));
        }
    }


    /**
     * 标题内容单元格
     * @param sheet
     * @param rowi
     * @param url
     * @param key
     * @param value
     * @param dto
     * @param cellStyle
     * @param cellStyle1
     */
    private void setCallValue(Sheet sheet, AtomicInteger rowi,String url, String key,String value,Set<String> dto,CellStyle cellStyle,CellStyle cellStyle1) {
        Row row = sheet.getRow(rowi.get());
        if (null ==row){
            row= sheet.createRow(rowi.get());
            //设置单边创建单元表格
            createCell(cellStyle, row);
            row.getCell(0).setCellValue(key+":");
            sheet.addMergedRegion(new CellRangeAddress(rowi.get(),rowi.get(),0,2));
            switch (key){
                case "*Http Method":
                    row.createCell(3).setCellValue(dto.stream().collect(Collectors.joining(",")).toUpperCase());
                    break;
                case "*服务接口地址":
                    row.createCell(3).setCellValue(url);
                    break;
                case "*服务接口编号":
                    row.createCell(3).setCellValue(urlMap.get(url));
                    break;
                default:
                    row.createCell(3).setCellValue(value);
                    break;
            }
            row.getCell(3).setCellStyle(cellStyle1);
            sheet.addMergedRegion(new CellRangeAddress(rowi.get(),rowi.get(),3,5));
        }
    }

    private void createCell(CellStyle cellStyle, Row row) {
        for (int i = 0; i <column ; i++) {
            row.createCell(i).setCellStyle(cellStyle);
        }
    }

    /**
     * 获取副本接口基础信息
     * @return
     */
    private Map<String,Map<Object,Object>> getInterfaceInfo() {
        Map<String,Map<Object,Object>> map=new HashMap<>();
        map.put("服务基本信息",MapUtil.builder().put("*服务接口编号","").put("*服务接口地址","").put("*服务提供方系统","A2中台").put("*调用方系统","").map());
        map.put("服务类型信息",MapUtil.builder().put("*协议类型","Rest API").put("* Http/Https","HTTPS").put("*Http Method","").map());
        map.put("服务地址信息",MapUtil.builder().put("*DEV地址", "").put("* PROD地址", "").put("中台DEV地址","").put("接口中台PROD地址", "").map());
        return map;
    }

    /**
     * 字段Table 标题
     * @return
     */
    private List<String> getTableTitle(){
        return Arrays.asList("序号","参数名","参数值/字段取值说明","字段类型","是否必填","备注");
    }


    /**
     * 设置一行空白数据 和或者 备注
     * @param sheet
     * @param rowi
     * @param name
     */
    private void setRemarks(Sheet sheet,AtomicInteger rowi,String name){
        Row row = sheet.getRow(rowi.get());
        if (null ==row){
            row=sheet.createRow(rowi.get());
            row.createCell(0).setCellValue(name);
            sheet.addMergedRegion(new CellRangeAddress(rowi.get(),rowi.get(),0,5));
        }
        rowi.getAndIncrement();
    }

    /**
     * 设置一行空白数据 和或者 备注
     * @param sheet
     * @param rowi
     * @param name
     */
    private void setRemarks(Sheet sheet,AtomicInteger rowi,String name,CellStyle cellStyle){
        Row row = sheet.createRow(rowi.get());
        createCell(cellStyle, row);
        row.getCell(0).setCellValue(name);
        sheet.addMergedRegion(new CellRangeAddress(rowi.get(),rowi.get(),0,5));
        rowi.getAndIncrement();
    }


    /**
     * 设置标题
     * @param sheet
     * @param rowi
     */
    private void setTableHeadertitle(Sheet sheet,AtomicInteger rowi,CellStyle cellStyle1){
        List<String> tableTitle = getTableTitle();
        setCallValue(sheet,rowi,tableTitle,cellStyle1);
    }
    /**
     * 设置标题
     * @param sheet
     * @param rowi
     */
    private void setTableHeaderData(Sheet sheet,AtomicInteger rowi,List<Parameters> parameters,CellStyle cellStyle){
        AtomicInteger k= new AtomicInteger(1);
        parameters.stream().filter(i->"header".equals(i.getIn())).forEach(i->{
            //江铃可以获取到字段类型    广汽不能   ,如果不能检查代码
            String type=StringUtils.isNotBlank(i.getType())?i.getType():"String";
            List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), i.getName(),i.getDescription(),type,String.valueOf(i.getRequired()),"");
            setCallValue(sheet,rowi,parameters1,cellStyle);
            k.getAndIncrement();
        });
    }

    /**
     * 处理入参数据，只解析query 和body 对象
     *  body对象只显示格式类型 ，详情最后补
     * @param sheet
     * @param rowi
     * @param parameters
     * @param swaggerDto
     * @param pendingData
     */
    private void setTableRequestData(Sheet sheet,AtomicInteger rowi,List<Parameters> parameters,SwaggerDto swaggerDto,List<String> pendingData,CellStyle cellStyle){
        //列表 序号 index
        AtomicInteger k= new AtomicInteger(1);
        parameters.stream().filter(i->reqestType.contains(i.getIn())).forEach(i->{
            //用body 和 query 来区分解析类型
            if ("body".equals(i.getIn()) && null !=i.getSchema()){
                String refLink="";
                //判断数据格式
                if (StringUtils.isNotBlank(i.getSchema().getType())){
                        //格式一  "items":{"$ref":"#/definitions/MailMsgSendDto"}
                        //格式二  "items":{"type":"integer","format":"int64"}
                    if (CollectionUtils.isNotEmpty(i.getSchema().getItems()) && StringUtils.isNotBlank(i.getSchema().getItems().get(0).getRef())){
                        //格式一   显示List<对象> list 格式
                        String getreflinkList = getRef(i.getSchema().getItems().get(0).getRef());
                        String type=i.getSchema().getType().equals("array")? getArrayType(getreflinkList) :null;
                        List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), i.getName(),"POJO",type,String.valueOf(i.getRequired()),"");
                        setCallValue(sheet,rowi,parameters1,cellStyle);
                        k.getAndIncrement();
                        //保存DTO对象
                        addPendingData(pendingData,getreflinkList);
                    }else{
                        //格式二  RequestParam 显示显示List<对象> list 格式
                        String type=i.getSchema().getType().equals("array")?getArrayType(i.getSchema().getItems().get(0).getType()):i.getSchema().getItems().get(0).getType();
                        List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), i.getName(),i.getDescription(),type,String.valueOf(i.getRequired()),"");
                        setCallValue(sheet,rowi,parameters1,cellStyle);
                        k.getAndIncrement();
                    }
                }else{
                    //当type为空时数据格式为："$ref":"#/definitions/MailMsgReadReqDto"
                    refLink=getRef(i.getSchema().getRef());
                }
                if (StringUtils.isNotBlank(refLink)){
                    //只显示数据格式对象，不显示详情
                    parseData(sheet, rowi, swaggerDto, k, refLink,cellStyle);
                }
            }else{
                if (StringUtils.isNotBlank(i.getName())){
                    List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), i.getName(),i.getDescription(),i.getType(),String.valueOf(i.getRequired()),"请求类型或为：@RequestParam ，所以没中文描述");
                    setCallValue(sheet,rowi,parameters1,cellStyle);
                    k.getAndIncrement();
                }
            }
        });
    }


    /**
     * 最后显示
     * @param sheet
     * @param rowi
     * @param swaggerDto
     * @param k
     * @param refLink
     */
    private void parseData(Sheet sheet, AtomicInteger rowi, SwaggerDto swaggerDto, AtomicInteger k, String refLink,CellStyle cellStyle) {
        Definitions definitions = swaggerDto.getDefinitions().get(refLink);
        List<String> childNodeList=new ArrayList<>();
        if (null !=definitions){
            Map<String, RespPropertiesDto> properties = definitions.getProperties();
            properties.forEach((k1,v1)->{
                String type=v1.getType();
                //判断子节点是否包含 叶子节点
                if (StringUtils.isBlank(v1.getType())) {
                    //存在一种情况，type 等于空，ref 不为空，Java代码为对象引用
                    String linkRef=getRef(v1.getRef());
                    if (StringUtils.isNotBlank(linkRef)){
                        type = linkRef;
                        childNodeList.add(linkRef);
                    }
                }else if ("array".equals(v1.getType())){
                    String linkRef=getRef(v1.getItems().getRef());
                    if (StringUtils.isNotBlank(linkRef)){
                        type = getArrayType(linkRef);
                        childNodeList.add(linkRef);
                    }else{
                        type = getArrayType(v1.getItems().getType());
                    }
                }
                //body 对象拿字段是否必填 已经尝试在@ApiModelProperty 上拿过了
                String description=StringUtils.isNotBlank(v1.getDescription())?v1.getDescription():"";
                List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), k1,description,type,"","");
                setCallValue(sheet, rowi,parameters1,cellStyle);
                k.getAndIncrement();
            });
        }
        //递归子节点 可能一个pojo中存在多个 对象引用
        // && !childNodeList.contains(refLink)  解决Dto引入当前dto问题
        if (CollectionUtils.isNotEmpty(childNodeList) && !childNodeList.contains(refLink)){
//            CompletableFuture.runAsync(()->{
            childNodeList.stream().forEach(node->{
                rowi.getAndIncrement();
                if(null !=swaggerDto.getDefinitions().get(node)){
                    setRemarks(sheet,rowi,node,HutoolExcelUtil.buildInterfaceInfoCellStyle(sheet.getWorkbook(), IndexedColors.PALE_BLUE.getIndex()));
                    setTableHeadertitle(sheet,rowi,HutoolExcelUtil.buildInterfaceInfoCellStyle(sheet.getWorkbook(), IndexedColors.PALE_BLUE.getIndex()));
                    k.set(1);
                    parseData(sheet,rowi,swaggerDto,k,node,cellStyle);
                    rowi.getAndIncrement();
                }
            });

//            });
        }
    }

    /**
     * 设置响应报文体
     * @param sheet
     * @param rowi
     * @param dto
     * @param swaggerDto
     * @param pendingData
     */
    private void setTableResponseData(Sheet sheet,AtomicInteger rowi,DocumentDto dto,SwaggerDto swaggerDto,List<String> pendingData,CellStyle cellStyle){
        dto.getResponses().forEach((key,value)->{
            //只拿到请求成功示例内容
            if (200 == key && "OK".equals(value.getDescription())){
                //获取响应报文
                String reflick=getRef(value.getSchema().getRef());
                Definitions definitions = swaggerDto.getDefinitions().get(reflick);
                if (null != definitions){
                   AtomicInteger k= new AtomicInteger(1);
                    definitions.getProperties().forEach((k1,v1)->{
                        //body 对象拿字段是否必填 已经尝试在@ApiModelProperty 上拿过了
                        String remarks="";
                        String type=v1.getType();
                        if (k1.equals("data")){
                            //判断是否为数组
                            if (StringUtils.isNotBlank(v1.getType()) && v1.getType().equals("array")){
                                //保存DTO对象
                                addPendingData(pendingData,getRef(v1.getItems().getRef()));
                                remarks=getRef(v1.getItems().getRef());
                            }
                            //判断response data对象是否有子节点
                            if (StringUtils.isNotBlank(v1.getRef())){
                                //保存DTO对象
                                addPendingData(pendingData,getRef(v1.getRef()));
                                type = getArrayType(getRef(v1.getRef()));
                            }
                        }
                        List<String> parameters1 = Arrays.asList(String.valueOf(k.get()), k1,StringUtils.isNotEmpty(v1.getDescription())?v1.getDescription():"",type,"",remarks);
                        setCallValue(sheet,rowi,parameters1,cellStyle);
                        k.getAndIncrement();
                    });
                }
            }
        });
    }


    /**
     * 设置一行数据
     * @param sheet
     * @param rowi
     * @param dataValue
     */
    private void setCallValue(Sheet sheet,AtomicInteger rowi,List<String> dataValue,CellStyle cellStyle){
        AtomicReference<Row> row = new AtomicReference<>(sheet.getRow(rowi.get()));
        if (null == row.get()){
            if (CollectionUtils.isNotEmpty(dataValue)){
                AtomicInteger k= new AtomicInteger(0);
                row.set(sheet.createRow(rowi.get()));
                dataValue.stream().forEach(i->{
                    row.get().createCell(k.get()).setCellValue(String.valueOf(i));
                    row.get().getCell(k.get()).setCellStyle(cellStyle);
                    k.getAndIncrement();
                });
            }
        }
        rowi.getAndIncrement();
    }

    /**
     * 解析链接Dto对象
     * @param refLink
     * @return
     */
    private String getRef(String refLink){
        if (StringUtils.isBlank(refLink)){
            return null;
        }
        return refLink.replace("#/definitions/","");
    }

    /**
     * 保存额外显示的对象
     * @param pendingData
     * @param refLink
     */
    private void addPendingData(List<String> pendingData,String refLink){
        if (StringUtils.isNotBlank(refLink)){
            pendingData.add(refLink);
        }
    }

    /**
     * 接口编号
     * @return
     */
    private static void createInterfaceNo(Map<String, Map<String,DocumentDto>> paths){
        AtomicInteger i= new AtomicInteger(1);
        paths.forEach((k,v)->{
            urlMap.put(k,"YX"+new DecimalFormat("00").format(i));
            i.getAndIncrement();
        });
    }

    /**
     * 获取数组类型
     * @param objecyType
     * @return
     */
    private String getArrayType(String objecyType) {
        return "List<" + objecyType + ">";
    }


    public static void main(String[] args) {

        String jsonString = "{\"_index\":\"book_shop\",\"_type\":\"it_book\",\"_id\":\"1\",\"_score\":1.0," +
                "\"_source\":{\"name\": \"Java编程思想（第4版）\",\"author\": \"[美] Bruce Eckel\",\"category\": \"编程语言\"," +
                "\"price\": 109.0,\"publisher\": \"机械工业出版社\",\"date\": \"2007-06-01\",\"tags\": [ \"Java\", \"编程语言\" ]}}";

        JSONObject object = JSONObject.parseObject(jsonString);
        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);

        System.out.println(pretty);
    }

}
