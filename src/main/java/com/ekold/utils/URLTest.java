package com.ekold.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/9/26
 */
public class URLTest {

    public static String url1 = "http://47.96.77.18/ks/ksxunlian1.php?km=%E9%A3%8E%E9%99%A9%E5%90%88%E8%A7%84%E8%80%83%E8%AF%95%E9%A2%98%E5%BA%93[saadmin]&lx=%E5%8D%95%E9%80%89&mm=MjV8T1RVNE5EUTMDA4MTYwMDI2fDE1Mzc5NDM1NDM1MjVzcWxbc3VPREkzW3NwXTg3BdNDIxMTgyMTk5";

//    public static void main(String[] args) {
//        try {
//            URL url = new URL(url1);
//            URLConnection URLconnection = url.openConnection();
//            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
//            int responseCode = httpConnection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                System.err.println("成功");
//                InputStream in = httpConnection.getInputStream();
//                InputStreamReader isr = new InputStreamReader(in);
//                BufferedReader bufr = new BufferedReader(isr);
//                String str;
//                while ((str = bufr.readLine()) != null) {
//                    System.out.println(str);
//                }
//                bufr.close();
//            } else {
//                System.err.println("失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {

        System.out.println("hello world !!!");

        try {
            //创建一个URL实例
            URL url = new URL(url1);

            try {
                //通过URL的openStrean方法获取URL对象所表示的自愿字节输入流
                InputStream is = url.openStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");

                //为字符输入流添加缓冲
                BufferedReader br = new BufferedReader(isr);
                String data = br.readLine();//读取数据

                while (data != null) {//循环读取数据
                    System.out.println(data);//输出数据
                    data = br.readLine();
                }
                br.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}