package com.webmagic.controller;

import com.webmagic.component.HtmlParserComponent;
import com.webmagic.processor.MarkdownSavePipeline;
import com.webmagic.processor.RunoobPageProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import us.codecraft.webmagic.Spider;

/**
 * @author Cheney
 * @title: WebMagicController
 * @projectName JR-WebMagic
 * @description: TODO
 * @date 2019/6/1315:38
 */
@AllArgsConstructor
@Controller
public class WebMagicController {

    private HtmlParserComponent htmlParserComponent;


    @RequestMapping(value = "/get/ting55",method = RequestMethod.POST)
    public void TestTing55(){
//        String book= "http://localhost:61675/v2/api-docs";
        String book = "http://gac-dev.amdu.dtyunxi.cn/gac/amdmp-boot-app-openapi/v2/api-docs";
        Spider.create(new RunoobPageProcessor())
                .addUrl(book)
                .addPipeline(new MarkdownSavePipeline())
                .thread(10)
                .run();
    }

}
