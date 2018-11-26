package com.ekold.threadpool;

import com.ekold.mapper.OrderInfoMapper;
import com.ekold.mapper.PayInfoMapper;
import com.ekold.mapper.UserInfoMapper;
import com.ekold.requests.OrderInfo;
import com.ekold.requests.PaymentInfos;
import com.ekold.requests.UserInfo;
import com.ekold.service.OrderService;
import com.ekold.service.TicketScheduledService;
import com.ekold.utils.TicketServerBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/10
 */
@Component
@Slf4j
public class AsyncTask {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TicketScheduledService ticketScheduledService;

    @Async("myTaskAsyncPool")
    public void taskAsync(String dateRemark, Integer startRow, Integer pageSize) throws InterruptedException, IOException {
        log.info("async start--!:{},{}", startRow, pageSize);

        UserInfo userInfo = new UserInfo();
        userInfo.setPageSize(pageSize);
        userInfo.setStartRow(startRow);

        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
        List<UserInfo> userInfoList = userInfoMapper.getUserInfo(userInfo);
        //获取ticket,如果ticket为空，则执行获取ticket方法
        if (StringUtils.isEmpty(TicketServerBean.getInstance().getService_ticket())) {
            ticketScheduledService.getTickets();
        }

        if (CollectionUtils.isNotEmpty(userInfoList)) {
            int done = 1;
            for (UserInfo user : userInfoList) {
                if (user.getMemberId() == null) {
                    continue;
                }
                log.info("current user: {},done:{}", user.getUserId(), done);
                user.setDateRemark(dateRemark);
                orderInfoList.addAll(orderService.getUserOrder(user));
                done++;
            }
        }

        if (CollectionUtils.isNotEmpty(orderInfoList)) {
            //转换时间类型
            orderService.formatTime(orderInfoList);
            List<PaymentInfos> paymentInfosList = orderService.buildPayInfo(orderInfoList);
            orderInfoMapper.addList(orderInfoList);
            payInfoMapper.addList(paymentInfosList);
        }

        log.info("async end --!:{},{}", startRow, pageSize);
    }
}