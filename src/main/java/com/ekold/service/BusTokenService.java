package com.ekold.service;

import com.ekold.utils.BusTokenBean;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BusTokenService {

    @Scheduled(cron = "* 0/30 * * * ? ")
    public void getTokens() throws Exception {
        BusTokenBean busTokenBean = BusTokenBean.getInstance();
        busTokenBean.setToken(this.auth());
    }

    public synchronized void getToken() throws Exception {
        this.getTokens();
    }


    //验证，获取token
    public String auth() throws Exception {

        String bus_username = "ynkmyd_10086";
        String bus_key = "10086_93kdj8wmdus";
        String host = "http://4006510871.cn/api/rest/";
        String bus_session = host + "session";
        Map<String, String> body = new HashMap<String, String>();

        String timestamp = String.valueOf(System.currentTimeMillis());            // 获取当前时间戳

        String[] args = {bus_username, bus_key, timestamp};                       // 构造签名哈希

        Arrays.sort(args);                                                        // 进行排序

        body.put("username", bus_username);
        body.put("signature", DigestUtils.sha1Hex(org.apache.commons.lang.StringUtils.join(args)));
        body.put("timestamp", timestamp);

        Gson gson = new Gson();
        // 将数据序列化
        String data = gson.toJson(body);

        // 调用 post 请求
        String resultJson = post(null, bus_session, org.apache.commons.lang.StringUtils.EMPTY, data, org.apache.commons.lang
                .StringUtils.EMPTY);
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
    private String post(String token, String url, String sub, String data, String sign) throws Exception {
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

}

