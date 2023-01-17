package com.webmagic.component;


import com.webmagic.service.WebMagicService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 文件工厂解析类
 */
@Component
public class HtmlParserComponent {

    private final WebMagicService webMagicService;

    public HtmlParserComponent(WebMagicService webMagicService) {
        this.webMagicService = webMagicService;
    }

    @Async
    public void ting55UrlParser(String link){
        System.getProperties().setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        try {
            WebDriver driver = new ChromeDriver();
            driver.get(link);
            WebElement webElement = driver.findElement(By.xpath("/html"));
            webElement.getAttribute("#jp_audio_0");
            Document doc = Jsoup.parse(webElement.getAttribute("outerHTML"));
            String fileName=doc.select(".h-play h1").text().replace("超级基因优化液有声小说","")+".mp3";
            String url=doc.getElementById("jp_audio_0").attr("src");
            String dir="d:/mp3/";
            webMagicService.saveUrlFile(url,fileName,dir);
            driver.close();
        }catch (Exception e){
            System.err.println("无法请求url==================="+link);
        }
    }
}
