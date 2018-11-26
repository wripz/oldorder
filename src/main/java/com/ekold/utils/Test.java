package com.ekold.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/3
 */
@Slf4j
public class Test {

    private static ParsePosition parsePosition = new ParsePosition(0);

    public static void main(String[] args) throws Exception {

        //System.out.println(TicketServerBean.getInstance().getService_ticket());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setCreateTimeDate(sdf.parse("20180228222433", parsePosition));
//        System.out.println(orderInfo.getCreateTimeDate());

//        System.out.println(130622 % 10000 == 0 ? 130622 / 10000 : (130622 / 10000 + 1));
        //System.out.println(auth());

        System.out.println(queryOrder(auth(), "B201711270008314"));

    }

    //验证，获取token
    public static String auth() throws Exception {

        String bus_username = "ynkmyd_10086";
        String bus_key = "10086_93kdj8wmdus";
        String host = "http://4006510871.cn/api/rest/";
        String bus_session = host + "session";
        Map<String, String> body = new HashMap<String, String>();

        String timestamp = String.valueOf(System.currentTimeMillis());            // 获取当前时间戳

        String[] args = {bus_username, bus_key, timestamp};                       // 构造签名哈希

        Arrays.sort(args);                                                        // 进行排序

        body.put("username", bus_username);
        body.put("signature", DigestUtils.sha1Hex(StringUtils.join(args)));
        body.put("timestamp", timestamp);

        Gson gson = new Gson();
        // 将数据序列化
        String data = gson.toJson(body);

        // 调用 post 请求
        String resultJson = post(null, bus_session, StringUtils.EMPTY, data, StringUtils.EMPTY);
        log.info("auth ResponseJson=>" + resultJson);
        // 将结果反序列化
        Map result = gson.fromJson(resultJson, Map.class);

        log.info("auth Map=>" + result);
        String token = "";
        if (result.containsKey("token")) {
            token = result.get("token").toString();
        }
        log.info("auth token=>" + token);
        return token;
    }

    //发送post请求
    private static String post(String token, String url, String sub, String data, String sign) throws Exception {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        String result = "";
        PostMethod method = new PostMethod(url + sub);
        try {
            RequestEntity entity = new StringRequestEntity(data, "application/json", "utf-8");
            method.setRequestEntity(entity);
            method.addRequestHeader("Authorization", token);
            method.addRequestHeader("Sign", sign);
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            client.executeMethod(method);
            result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
        } catch (Exception e) {
            log.error("bus post Exception", e);
        } finally {
            method.releaseConnection();
        }
        return result;
    }

    //查询汽车票订单
    public static String queryOrder(String token, String orderCode) throws Exception {
        log.info("queryOrder token=>" + token + " orderCode=>" + orderCode);
        String busOrderUrl = "http://4006510871.cn/api/rest/" + "bus/order";
        NameValuePair[] params = new NameValuePair[]{};

        //get请求
        String result = get(token, busOrderUrl, orderCode, params);
        log.info("queryOrder ResponseJson=>" + result);
        return result;
    }

    //发送get请求
    private static String get(String token, String url, String sub, NameValuePair[] pairs) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        String result = "";
        GetMethod method = new GetMethod(url + sub);
        try {
            method.addRequestHeader("Authorization", token);
            method.setQueryString(pairs);
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            client.executeMethod(method);
            result = IOUtils.toString(method.getResponseBodyAsStream(), "utf-8");
        } catch (Exception e) {
            log.error("bus get Exception", e);
        } finally {
            method.releaseConnection();
        }
        return result;
    }

    /**
     * 获取半个小时前的时间
     *
     * @return
     */
    public static String getTimeHour() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calNow = Calendar.getInstance();
        calNow.add(Calendar.MINUTE, -30);
        Date date = calNow.getTime();
        return sdfTime.format(date);

    }
}