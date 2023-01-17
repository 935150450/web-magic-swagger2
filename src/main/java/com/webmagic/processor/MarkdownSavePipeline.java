package com.webmagic.processor;

import org.springframework.beans.factory.annotation.Autowired;
import com.webmagic.service.WebMagicService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * @author Cheney
 * @title: MarkdownSavePipeline
 * @projectName JR-WebMagic
 * @description: TODO
 * @date 2019/6/1315:37
 */
public class MarkdownSavePipeline implements Pipeline {
    @Autowired
    private WebMagicService webMagicService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            String fileName = resultItems.get("fileName");
            String url = resultItems.get("url");
            String dir = resultItems.get("dir");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
