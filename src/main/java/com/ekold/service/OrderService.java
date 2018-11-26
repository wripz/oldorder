package com.ekold.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ekold.requests.OrderInfo;
import com.ekold.requests.PaymentInfos;
import com.ekold.requests.UserInfo;
import com.ekold.utils.TicketServerBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/12
 */
@Slf4j
@Service
public class OrderService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 取指定日期后的订单数据
     */
    public List<OrderInfo> getUserOrder(UserInfo user) throws IOException {
        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
        List<OrderInfo> resultList = new ArrayList<OrderInfo>();
        List<OrderInfo> tempList = null;
        //访问地址
        String orderList12580Url = "http://open.12580.com/open/order/user_orders?st={0}&member_id={1}&sort" +
                "=create_time&page_size=50&page_no={2}&closeStatus=0";

        Boolean flag = true;
        int pageNo = 1;
        while (flag) {

            String resp = this.doGet(MessageFormat.format(orderList12580Url, TicketServerBean.getInstance()
                    .getService_ticket(), user.getMemberId()
                    .toString(), String.valueOf(pageNo)));

            tempList = this.buildOrderList(resp);
            //如果查询结果小于50条，则不需要继续下一次查询
            if (CollectionUtils.isEmpty(tempList) || tempList.size() < 50) {
                flag = false;
            }

            if (CollectionUtils.isNotEmpty(tempList)) {
                orderInfoList.addAll(tempList);
            }
            pageNo++;
        }

        //取指定日期后的订单数据
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            String dateRemark = user.getDateRemark();
            if (StringUtils.isEmpty(dateRemark)) {
                return null;
            }
            for (OrderInfo result : orderInfoList) {
                if (result.getCreateTime().compareTo(dateRemark) >= 0) {
                    resultList.add(result);
                }
            }
        }

        return resultList;
    }

    public String doGet(String url) throws IOException {
        //1.使用默认的配置的httpclient
        CloseableHttpClient client = HttpClients.createDefault();
        //2.使用get方法
        HttpGet httpGet = new HttpGet(url);
        //3.执行请求，获取响应
        CloseableHttpResponse response = client.execute(httpGet);

        //请求发送成功，并得到响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            //读取服务器返回过来的json字符串数据
            HttpEntity entity = response.getEntity();
            String strResult = EntityUtils.toString(entity, "utf-8");

            return strResult;
        } else {
            log.error("get请求提交失败:" + url);
        }
        return null;
    }

    public List<OrderInfo> buildOrderList(String resp) {
        List<OrderInfo> orderInfoList = null;
        if (StringUtils.isNotEmpty(resp)) {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(resp);
            if (jsonObject != null) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array != null && array.size() > 0) {
                    orderInfoList = JSON.parseArray(array.toString(), OrderInfo.class);
                }
            }
        }
        return orderInfoList;
    }

    public void formatTime(List<OrderInfo> orderInfoList) {
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            for (OrderInfo orderInfo : orderInfoList) {
                orderInfo.setCreateTimeDate(sdf.parse(orderInfo.getCreateTime(), new ParsePosition(0)));
                orderInfo.setPayTimeDate(sdf.parse(orderInfo.getPayTime(), new ParsePosition(0)));
            }
        }
    }


    public List<PaymentInfos> buildPayInfo(List<OrderInfo> orderInfoList) {
        List<PaymentInfos> resultList = null;
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            resultList = new ArrayList<PaymentInfos>();
            for (OrderInfo orderInfo : orderInfoList) {
                for (PaymentInfos paymentInfos : orderInfo.getPaymentInfos()) {
                    paymentInfos.setOrderId(orderInfo.getId());
                }
                resultList.addAll(orderInfo.getPaymentInfos());
            }
        }
        return resultList;
    }

}