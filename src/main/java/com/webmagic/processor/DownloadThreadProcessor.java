package com.webmagic.processor;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Cheney
 * @title: DownloadThread
 * @projectName JR-WebMagic
 * @description: TODO
 * @date 2019/6/1410:27
 */
public class DownloadThreadProcessor  extends Thread {
    //线程id
    private int threadId;
    private int StartPos;
    private int EndPos;
    private String FileURL;
    private File SaveFile;

    public DownloadThreadProcessor(int threadId, int StartPos, int EndPos, String FileURL, File SaveFile){
        this.threadId = threadId;
        this.StartPos = StartPos;
        this.EndPos = EndPos;
        this.FileURL = FileURL;
        this.SaveFile = SaveFile;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(FileURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(500);
            conn.setRequestMethod("GET");
            //请求文件段
            conn.setRequestProperty("Range", "bytes=" + StartPos + "-" + EndPos);
            int code = conn.getResponseCode();
            //206表示文件分段请求,而不是整个文件请求
            if(code == 206){
                InputStream is = conn.getInputStream();
                int len = 0;
                byte[] buf = new byte[1024];
                RandomAccessFile raf = new RandomAccessFile(SaveFile, "rw");
                raf.seek(StartPos);
                while((len = is.read(buf)) > 0) {
                    raf.write(buf, 0, len);
                }
                is.close();
                System.out.println("线程" + threadId + "下载完毕！！");
            }else{
                System.out.println("不支持分段下载");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
