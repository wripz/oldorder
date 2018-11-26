package com.ekold.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/9/26
 */
public class GetSubjectJudge {


    static String url = "http://47.96.77.18/ks/ksxunlian1.php?km=%E9%A3%8E%E9%99%A9%E5%90%88%E8%A7%84%E8%80%83%E8%AF%95%E9%A2" +
            "%98%E5%BA%93[saadmin]&lx=%E5%88%A4%E6%96%AD&mm=xPVE13TXpVM05gxNjAwMjZ8MTUzNzk1NDM1N3N3NxbFtzcF00MqVXdbc3BdODc" +
            "=jExODIxOTkwMD";

    public static Map<String, String> map = new HashMap<String, String>();

    public static List<SubjectVO> subjectVOList = new ArrayList<SubjectVO>();

    public static void main(String[] args) throws Exception {
        buildText(sendGet());
    }

    public static void buildText(List<SubjectVO> voList) throws Exception {
        String path = "d:\\判断.txt";
        File file = new File(path);
        file.createNewFile();

        // write
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuffer buffer = new StringBuffer();
        int i = 1;
        for (SubjectVO subjectVO : voList) {
            buffer.append(i);
            buffer.append(".");
            buffer.append(subjectVO.getSubject());
            buffer.append("\r\n");
            buffer.append("\r\n");
            buffer.append("正确答案:").append(subjectVO.getAnswer());
            buffer.append("\r\n");
            buffer.append("\r\n");
            i++;
        }
        System.out.println(i);
        bw.write(buffer.toString());
        bw.flush();
        bw.close();
        fw.close();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @return URL 所代表远程资源的响应结果
     */
    public static List<SubjectVO> sendGet() throws Exception {

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Referer", "http://47.96.77.18/ks/ksxunlian" +
                ".php?mm=xPVE13TXpVM05gxNjAwMjZ8MTUzNzk1NDM1N3N3NxbFtzcF00MqVXdbc3BdODc=jExODIxOTkwMD");

        for (int i = 0; i < 30; i++) {

            HttpResponse response = httpClient.execute(httpGet);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            Elements subjectAll = doc.select("div[class=ss3]");

            for (Element element : subjectAll) {
                SubjectVO vo = new SubjectVO();
                vo.setSubject(element.select("B").get(0).html());
                vo.setAnswer(element.getElementsByAttributeValue("class", "ss4").get(0).html());
                if (map.get(vo.getSubject()) == null) {
                    subjectVOList.add(vo);
                    map.put(vo.getSubject(), "1");
                }
            }
        }


        return subjectVOList;
    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, List<SubjectVO> voList, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        int i = 0;
        for (SubjectVO vo : voList) {
            row = sheet.createRow(i + 1);
            //将内容按顺序赋给对应的列对象
            row.createCell(0).setCellValue(vo.getSubject());
            row.createCell(1).setCellValue(vo.getOptionA());
            row.createCell(2).setCellValue(vo.getOptionB());
            row.createCell(3).setCellValue(vo.getOptionC());
            row.createCell(4).setCellValue(vo.getOptionD());
            i++;
        }

        return wb;
    }
}