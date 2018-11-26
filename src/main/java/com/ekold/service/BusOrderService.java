package com.ekold.service;

import com.alibaba.fastjson.JSON;
import com.ekold.mapper.BusOrderInfoMapper;
import com.ekold.requests.BusOrderInfo;
import com.ekold.utils.BusTokenBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/12
 */
@Slf4j
@Service
public class BusOrderService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    private BusOrderInfoMapper busOrderInfoMapper;

    @Autowired
    private BusTokenService busTokenService;

    /**
     * 获取用户汽车票订单数据
     */
    public List<BusOrderInfo> getUserOrder(BusOrderInfo busOrderInfo) throws Exception {

        if (busOrderInfo == null || StringUtils.isEmpty(busOrderInfo.getUserid())) {
            return null;
        }

        //检查token
        if (StringUtils.isEmpty(BusTokenBean.getInstance().getToken())) {
            busTokenService.getToken();
        }

        List<BusOrderInfo> resultList = null;
        BusOrderInfo result = null;
        //获取用户汽车票订单信息
        busOrderInfo.setTimeRemark(this.getTimeHour());
        List<BusOrderInfo> busOrderInfoList = busOrderInfoMapper.findBusOrderList(busOrderInfo);
        if (CollectionUtils.isNotEmpty(busOrderInfoList)) {
            resultList = new ArrayList<BusOrderInfo>();
            for (BusOrderInfo param : busOrderInfoList) {
                result = this.getBusOrder(param);
                if (result != null && !StringUtils.equals("404", result.getErrcode()) && !"400".equals(result.getErrcode())) {
                    resultList.add(result);
                }
            }
        }

        return resultList;
    }

    public BusOrderInfo getBusOrder(BusOrderInfo busOrderInfo) throws Exception {

        //检查token
        if (StringUtils.isEmpty(BusTokenBean.getInstance().getToken())) {
            busTokenService.getToken();
        }

        return JSON.parseObject(this.queryOrder(BusTokenBean.getInstance().getToken(), busOrderInfo.getOrdercode()),
                BusOrderInfo.class);
    }

    /**
     * 获取用户汽车票订单数据
     */
    public String getOriginalUserOrder(BusOrderInfo busOrderInfo) throws Exception {

        if (busOrderInfo == null || StringUtils.isEmpty(busOrderInfo.getUserid())) {
            return null;
        }

        //检查token
        if (StringUtils.isEmpty(BusTokenBean.getInstance().getToken())) {
            busTokenService.getToken();
        }

        List<BusOrderInfo> resultList = null;

        //获取用户汽车票订单信息
        busOrderInfo.setTimeRemark(this.getTimeHour());
        List<BusOrderInfo> busOrderInfoList = busOrderInfoMapper.findBusOrderList(busOrderInfo);
        if (CollectionUtils.isNotEmpty(busOrderInfoList)) {
            for (BusOrderInfo param : busOrderInfoList) {
                return this.queryOrder(BusTokenBean.getInstance().getToken(), param.getOrdercode());
            }
        }

        return null;
    }

    //查询汽车票订单
    public String queryOrder(String token, String orderCode) throws Exception {
        log.info("queryOrder token=>" + token + " orderCode=>" + orderCode);
        String busOrderUrl = "http://4006510871.cn/api/rest/" + "bus/order";
        NameValuePair[] params = new NameValuePair[]{};

        //get请求
        String result = get(token, busOrderUrl, orderCode, params);
        log.info("queryOrder ResponseJson=>" + result);
        return result;
    }

    //发送get请求
    private String get(String token, String url, String sub, NameValuePair[] pairs) {
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
    public String getTimeHour() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calNow = Calendar.getInstance();
        calNow.add(Calendar.MINUTE, -30);
        Date date = calNow.getTime();
        return sdfTime.format(date);

    }
}