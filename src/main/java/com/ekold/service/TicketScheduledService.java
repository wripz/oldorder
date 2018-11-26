package com.ekold.service;

import com.ekold.utils.TicketServerBean;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
public class TicketScheduledService {

    @Scheduled(cron = "* 0/30 * * * ? ")
    public void getTickets() throws UnknownHostException {
        String hostIp = this.getHostIp();
        String updateIp = "10.209.176.15";
        log.info("getTicket hostIp=>" + hostIp + " updateIp=>" + updateIp);
        //if (StringUtils.isNotBlank(hostIp) && hostIp.equals(updateIp)) {
        TicketServerBean tsb = TicketServerBean.getInstance();
        log.info("before update service ticket TSB=>" + tsb);
        this.getTicket();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("Date=>" + format.format(new Date()) + " after update service ticket TSB=>" + tsb);
        //}
    }


    public synchronized static void getTicket() {
        int count = 0;
        int ret = -1;
        while (ret != 0 && count < 10) {
            ret = doGetTicket();
            if (ret != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("getTicket exception:", e1);
                }
            }
            count++;
            log.info("getTicket ret=>" + ret + " count=>" + count);
        }
        TicketServerBean tsb = TicketServerBean.getInstance();
        log.info("getTicket Service_ticket=>" + tsb.getService_ticket() + " Expire_time=>" + tsb.getExpire_time());
    }


    public static int doGetTicket() {
        TicketServerBean tsb = TicketServerBean.getInstance();
        long timstamp = System.currentTimeMillis();
        String app_id = "5300110001";
        String app_key = "d25b5904aec84b18826694b451d34a87";
        String nonce = getRandomString(6);
        log.info("app_id:=" + app_id + " ,app_key:=" + app_key + " ,timstamp:=" + timstamp + " ,nonce:=" + nonce);
        String sig = hmacSha1(app_id + timstamp + nonce, app_key);
        log.info("sig:=" + sig);
        String serUrl = "http://open.12580.com/open/";
        String ticketPath = "auth/ticket";
        String url = serUrl + ticketPath + "?appid=" + app_id + "&timestamp=" + timstamp + "&nonce=" + nonce + "&sig=" + sig;
        log.info("url:=" + url);
        String tickets = getRestful(url, null, "GET");
        log.info("getTicket response:" + tickets);
        int ret = -1;
        if (StringUtils.isNotBlank(tickets)) {
            JSONObject jsonObject = JSONObject.fromObject(tickets);
            ret = jsonObject.getInt("ret");
            if (ret == 0) {
                Long expire_time = jsonObject.getLong("expire_time");
                long millseconds = expire_time - System.currentTimeMillis();
                int redisTtl = (int) (millseconds / 1000 - 60);
                String service_ticket = jsonObject.getString("service_ticket");
                tsb.setExpire_time(expire_time);
                tsb.setService_ticket(service_ticket);
                log.info("doGetTicket Service_ticket=>" + tsb.getService_ticket() + " Expire_time=>" + tsb.getExpire_time()
                        + " validTime=>" + millseconds + "millseconds" + " redisTTL=>" + redisTtl + "seconds tsb=>" + tsb);
            }
        }
        return ret;
    }


    /**
     * @param length
     * @return
     */
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * hmacsha1算法
     *
     * @param source 源字串
     * @param key    key
     * @return 摘要结果
     */
    public static String hmacSha1(String source, String key) {

        try {

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");

            mac.init(keySpec);

            byte[] result = mac.doFinal(source.getBytes());

            return Base64.getEncoder().encodeToString(result);

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }

    }


    /**
     * @param url
     * @param body
     * @param method
     * @return
     */
    public static String getRestful(String url, String body, String method) {
        return getRestful(url, body, method, "application/json", "application/json");
    }

    public static String getRestful(String url, String body, String method, String contentType, String accept) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", accept);
        headers.put("Content-Type", contentType);
        return getRestful(url, body, method, headers);
    }

    public static String getRestful(String url, String body, String method, Map<String, String> headers) {
        String result = "";
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod(method);
            if (headers != null && headers.size() > 0) {
                Set<Map.Entry<String, String>> entrys = headers.entrySet();
                for (Map.Entry<String, String> entry : entrys) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            if (null != body) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(body.getBytes());
                outputStream.flush();
            }

            if (connection.getResponseCode() != 200) {
                int responseCode = connection.getResponseCode();
                throw new Exception("ResponseCode Error:" + responseCode);
            }

            BufferedReader responseBuffer = new BufferedReader(
                    new InputStreamReader((connection.getInputStream())));

            StringBuffer response = new StringBuffer();
            String output = "";
            while ((output = responseBuffer.readLine()) != null) {
                response.append(output);
            }
            result = response.toString();
            log.info("GetRestful Result=>Url:" + url + " Method:" + method + " Headers:" + headers + " Body:" + body + " " +
                    "Result:" + result);
        } catch (Exception e) {
            log.error("GetRestful Exception=>Url:" + url + " Method:" + method + " Headers:" + headers + " Body:" + body + " " +
                    "Msg:" + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;

    }

    // 获取主机IP
    public static String getHostIp() throws UnknownHostException {
        String ip = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            log.info("getHostIp=>" + ip);
        } catch (UnknownHostException e) {
            log.error("getHostIp Exception", e);
        }
        return ip;
    }

}

