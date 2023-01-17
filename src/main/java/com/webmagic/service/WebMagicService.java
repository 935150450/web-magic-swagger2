package com.webmagic.service;

import com.webmagic.processor.DownloadThreadProcessor;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

@Logger
@Service
public class WebMagicService {

    public static File SaveFile;
    public final static int COUNT = 10;      //分段数
    /**
     *
     * 每个线程下载的字节数
     */
    private long unitSize = 1000 * 1024;

    /**
     * 公有方法,将body解析为markdown文本
     * @param article #content内容
     * @return markdown文本
     */
    public static String markdown(Element article){
        StringBuilder markdown = new StringBuilder();
        //article.children().forEach(it ->parseEle(markdown, it, 0));
        return markdown.toString();
    }

    /**
     * 保存为本地文件
     * @param direct 文件夹
     * @param fileName 文件名
     * @param content 保存的内容
     */
    public static void saveFile(String content,String fileName,String direct) throws IOException {
        File dir = new File(direct);
        if(!dir.exists() || dir.isFile()) {
            if(!dir.mkdirs()){
                throw new RuntimeException("创建文件夹失败");
            }
        }
        File file = new File(dir,fileName);
        if(!file.exists() || file.isDirectory()){
            if(!file.createNewFile()) {
                throw new RuntimeException("创建文件失败");
            }
        }
        PrintWriter pw = new PrintWriter(file);
        pw.write(content);
        pw.close();
        System.out.println("文件创建成功"+file.getName());
    }

    /**
     * 保存为本地文件
     * @param direct 文件夹
     * @param fileName 文件名
     * @param link 文件地址
     */
    public void saveUrlFile(String link, String fileName, String direct) throws Exception {
        doDownload(link,fileName,direct);
    }

    /**
     *
     * 启动多个线程下载文件
     */
    public void  doDownload(String remoteFileUrl,String fileName,String localPath) throws IOException {
        URL url = new URL(remoteFileUrl);
        //建立一个连接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int code = conn.getResponseCode();
        //String fileName = FileURL.substring(position+1);   //文件名
        System.out.println("file name is :"+fileName);
        if(code == 200){
            int FileLength = conn.getContentLength();  //获取文件长度
            System.out.println("文件总长度为："+FileLength);

            SaveFile = new File(localPath+fileName);
            RandomAccessFile raf = new RandomAccessFile(SaveFile,"rw"); //若没有该文件，则自动创建
            raf.setLength(FileLength);  //设置文件长度
            raf.close();

            //分块大小
            int blockSize = FileLength / COUNT;
            for(int i=0; i <= COUNT; i++){
                int StartPos = i * blockSize;
                int EndPos = (i+1) * blockSize - 1;

                //最后一条线程EndPos = FileLength
                if(i == COUNT){
                    EndPos = FileLength;
                }
                System.out.println("线程" + i + "下载的部分为：" + StartPos +"---" + EndPos);
                new DownloadThreadProcessor(i,StartPos,EndPos,remoteFileUrl,SaveFile).start();
            }

        }
    }


    /**
     * 创建文件夹
     * @param direct
     * @param fileName
     * @return
     * @throws Exception
     */
    private static File createDirect(String direct, String fileName)throws Exception{
        File dir = new File(direct);
        if(!dir.exists() || dir.isFile()) {
            if(!dir.mkdirs()){
                throw new RuntimeException("创建文件夹失败");
            }
        }
        File file = new File(dir,fileName);
        if(!file.exists() || file.isDirectory()){
            if(!file.createNewFile()) {
                throw new RuntimeException("创建文件失败");
            }
        }
        return file;
    }

}
