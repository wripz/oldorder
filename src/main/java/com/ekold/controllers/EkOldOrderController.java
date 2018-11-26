package com.ekold.controllers;

import com.ekold.mapper.OrderInfoMapper;
import com.ekold.mapper.PayInfoMapper;
import com.ekold.mapper.UserInfoMapper;
import com.ekold.requests.OrderInfo;
import com.ekold.requests.PaymentInfos;
import com.ekold.requests.UserInfo;
import com.ekold.service.OrderService;
import com.ekold.service.TicketScheduledService;
import com.ekold.threadpool.AsyncTask;
import com.ekold.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author:yangqiao
 * @description:
 * @Date:2018/1/2
 */
@RestController
@RequestMapping("/ekOldOrder")
@Slf4j
public class EkOldOrderController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private TicketScheduledService ticketScheduledService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AsyncTask asyncTask;

    @Cacheable(value = "orderInfo", key = "#userInfo.userId")
    @PostMapping(value = "/findOrderByUserId")
    public BaseResponse findOrderByUserId(@RequestBody UserInfo userInfo) throws IOException {
        BaseResponse baseResponse = null;
        baseResponse = BackResponseUtil.getBaseResponse(ReturnCodeEnum.CODE_1000.getCode());
        List<OrderInfo> orderInfoList = null;
        if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUserId())) {
            //先组装查询参数
            OrderInfo orderInfo = this.buildParams(userInfo);

            //查询订单信息
            orderInfoList = orderInfoMapper.findOrderList(orderInfo);
            //获取订单支付信息
            if (CollectionUtils.isNotEmpty(orderInfoList)) {
                for (OrderInfo param : orderInfoList) {
                    param.setPaymentInfos(this.findOrderPayInfo(param));
                }
            }
            baseResponse.setDataInfo(orderInfoList);
        }else {
            baseResponse = BackResponseUtil.getBaseResponse(ReturnCodeEnum.CODE_1006.getCode());
        }

        return baseResponse;
    }

    /**
     * 组装查询参数
     */
    private OrderInfo buildParams(UserInfo userInfo) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setUserId(Long.parseLong(userInfo.getUserId()));
        orderInfo.setId(userInfo.getOrderId());


        //默认每页查询3条数据
        if (userInfo.getPageSize() == null) {
            userInfo.setPageSize(3);
        }
        //默认第1页
        if (userInfo.getPageNumber() == null) {
            userInfo.setPageNumber(1);
        }
        //开始位置
        orderInfo.setStartRow((userInfo.getPageNumber() - 1) * userInfo.getPageSize());
        orderInfo.setPageSize(userInfo.getPageSize());

        return orderInfo;
    }

    private List<PaymentInfos> findOrderPayInfo(OrderInfo param) {
        if (param != null && param.getId() != null) {
            PaymentInfos paymentInfos = new PaymentInfos();
            paymentInfos.setOrderId(param.getId());
            return payInfoMapper.findPayInfoList(paymentInfos);
        }

        return new ArrayList<PaymentInfos>();
    }

    @PostMapping(value = "/getOrderJsonByUserId")
    public String getOrderJsonByUserId(@RequestBody UserInfo userInfo) throws IOException {

        if (userInfo != null) {
            List<UserInfo> userInfoList = userInfoMapper.getUserInfo(userInfo);

            //获取ticket,如果ticket为空，则执行获取ticket方法
            if (StringUtils.isEmpty(TicketServerBean.getInstance().getService_ticket())) {
                ticketScheduledService.getTickets();
            }

            if (CollectionUtils.isNotEmpty(userInfoList)) {
                for (UserInfo user : userInfoList) {
                    if (user.getMemberId() == null) {
                        continue;
                    }

                    user.setDateRemark(userInfo.getDateRemark());
                    return JSONUtils.toJson(orderService.getUserOrder(user));
                }
            }
        }

        return null;
    }

    @PostMapping(value = "/deleteAll")
    public void deleteAll() throws IOException {
        orderInfoMapper.delete();
        payInfoMapper.delete();
    }

    @PostMapping(value = "/deleteByUserId")
    public void deleteByUserId(@RequestBody UserInfo userInfo) throws IOException {

        if (userInfo == null || StringUtils.isEmpty(userInfo.getUserId())) {
            return;
        }

        //根据入参查询用户信息
        List<UserInfo> userInfoList = userInfoMapper.getUserInfo(userInfo);
        if (CollectionUtils.isNotEmpty(userInfoList)) {
            for (UserInfo param : userInfoList) {
                if (param.getMemberId() == null) {
                    continue;
                }
                orderInfoMapper.deleteByUserId(param);
                payInfoMapper.deleteByOrderId(param);
            }
        }
    }

    @PostMapping(value = "/getOldOrderByUserId")
    public List<OrderInfo> getOldOrderByUserId(@RequestBody UserInfo userInfo) throws IOException {

        if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUserId())) {
            List<UserInfo> userInfoList = userInfoMapper.getUserInfo(userInfo);

            //获取ticket,如果ticket为空，则执行获取ticket方法
            if (StringUtils.isEmpty(TicketServerBean.getInstance().getService_ticket())) {
                ticketScheduledService.getTickets();
            }

            if (CollectionUtils.isNotEmpty(userInfoList)) {
                for (UserInfo user : userInfoList) {
                    if (user.getMemberId() == null) {
                        continue;
                    }
                    user.setDateRemark(userInfo.getDateRemark());
                    return orderService.getUserOrder(user);
                }
            }
        }

        return null;
    }

    @PostMapping(value = "/getOldOrder")
    public List<OrderInfo> getOldOrder(@RequestBody UserInfo userInfo) throws IOException {

        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
        if (userInfo != null) {
            List<UserInfo> userInfoList = userInfoMapper.getUserInfo(userInfo);
            //获取ticket,如果ticket为空，则执行获取ticket方法
            if (StringUtils.isEmpty(TicketServerBean.getInstance().getService_ticket())) {
                ticketScheduledService.getTickets();
            }

            if (CollectionUtils.isNotEmpty(userInfoList)) {
                for (UserInfo user : userInfoList) {
                    if (user.getMemberId() == null) {
                        continue;
                    }
                    log.info("current user: {}", user.getUserId());
                    user.setDateRemark(userInfo.getDateRemark());
                    orderInfoList.addAll(orderService.getUserOrder(user));
                }
            }
        }

        return orderInfoList;
    }

    @PostMapping(value = "/fetchOrderByBatch")
    public void fetchOrderByBatch(@RequestBody UserInfo userInfo) throws IOException, InterruptedException {

        Integer userCount = userInfoMapper.getUserCount(userInfo);

        if (userCount != null && userCount > 0) {
            for (int i = 0; i < ((userCount % 10000 == 0) ? userCount / 10000 : (userCount / 10000 + 1)); i++) {
                asyncTask.taskAsync(userInfo.getDateRemark(), i * 10000, 10000);
            }
        }
    }

    @PostMapping(value = "/fetchOrder")
    public String fetchOrder(@RequestBody UserInfo userInfo) throws IOException {
        List<OrderInfo> orderInfoList = this.getOldOrder(userInfo);
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            //转换时间类型
            orderService.formatTime(orderInfoList);
            List<PaymentInfos> paymentInfosList = orderService.buildPayInfo(orderInfoList);
            orderInfoMapper.addList(orderInfoList);
            payInfoMapper.addList(paymentInfosList);
        }

        return null;
    }

    @PostMapping(value = "/fetchOrderByUserId")
    public String fetchOrderByUserId(@RequestBody UserInfo userInfo) throws IOException {
        List<OrderInfo> orderInfoList = this.getOldOrderByUserId(userInfo);
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            //转换时间类型
            orderService.formatTime(orderInfoList);
            List<PaymentInfos> paymentInfosList = orderService.buildPayInfo(orderInfoList);
            orderInfoMapper.addList(orderInfoList);
            payInfoMapper.addList(paymentInfosList);
        }

        return null;
    }

    @PostMapping(value = "/fetch12580Order")
    public String fetch12580Order(@RequestBody UserInfo userInfo) throws IOException {
        List<OrderInfo> orderInfoList = this.getOldOrder(userInfo);
        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            //转换时间类型
            orderService.formatTime(orderInfoList);
            List<PaymentInfos> paymentInfosList = orderService.buildPayInfo(orderInfoList);
            orderInfoMapper.addList(orderInfoList);
            payInfoMapper.addList(paymentInfosList);
        }

        return null;
    }

    @PostMapping(value = "/getUserInfoByUserId")
    public List<UserInfo> getUserInfoByUserId(@RequestBody UserInfo userInfo) {
        //根据用户id获取用户信息
        if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUserId())) {
            return userInfoMapper.getUserInfo(userInfo);
        }

        return null;
    }

}