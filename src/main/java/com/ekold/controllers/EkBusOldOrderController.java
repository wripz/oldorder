package com.ekold.controllers;

import com.ekold.mapper.BusInsuranceInfoMapper;
import com.ekold.mapper.BusOrderInfoMapper;
import com.ekold.mapper.BusStationInfoMapper;
import com.ekold.mapper.OldBusOrderInfoMapper;
import com.ekold.requests.BusInsuranceInfo;
import com.ekold.requests.BusOrderInfo;
import com.ekold.requests.BusStationInfo;
import com.ekold.requests.UserInfo;
import com.ekold.service.BusOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/ekOldBusOrder")
@Slf4j
public class EkBusOldOrderController {

    @Autowired
    private OldBusOrderInfoMapper oldBusOrderInfoMapper;

    @Autowired
    private BusOrderInfoMapper busOrderInfoMapper;

    @Autowired
    private BusInsuranceInfoMapper busInsuranceInfoMapper;

    @Autowired
    private BusStationInfoMapper busStationInfoMapper;

    @Autowired
    private BusOrderService busOrderService;

    @PostMapping(value = "/findBusOrderByUserId")
    public List<BusOrderInfo> findBusOrderByUserId(@RequestBody UserInfo userInfo) throws IOException {
        log.info("findBusOrderByUserId.pageCurrent:{}", userInfo.getPageNumber());
        List<BusOrderInfo> busOrderInfoList = null;
        if (userInfo != null && StringUtils.isNotEmpty(userInfo.getUserId())) {
            //组装查询参数
            BusOrderInfo busOrderInfo = this.buildParams(userInfo);

            //先查询订单信息
            busOrderInfoList = oldBusOrderInfoMapper.findBusOrderList(busOrderInfo);
            //获取保险信息和汽车站信息
            if (CollectionUtils.isNotEmpty(busOrderInfoList)) {
                for (BusOrderInfo param : busOrderInfoList) {
                    //获取保险信息
                    param.setInsuranceInfo(this.findInsuranceInfo(param));
                    //获取车站信息
                    param.setBusStationInfo(this.findBusStationInfo(param));
                }
            }
        }

        return busOrderInfoList;
    }

    /**
     * 组装查询参数
     */
    private BusOrderInfo buildParams(UserInfo userInfo) {

        BusOrderInfo busOrderInfo = new BusOrderInfo();
        busOrderInfo.setUserid(userInfo.getUserId());

        //默认每页查询3条数据
        if (userInfo.getPageSize() == null) {
            userInfo.setPageSize(3);
        }
        //默认第1页
        if (userInfo.getPageNumber() == null) {
            userInfo.setPageNumber(1);
        }
        //计算当前页开始查找的位置
        busOrderInfo.setStartRow((userInfo.getPageNumber() - 1) * userInfo.getPageSize());
        busOrderInfo.setPageSize(userInfo.getPageSize());

        return busOrderInfo;
    }

    private BusStationInfo findBusStationInfo(BusOrderInfo param) {
        BusStationInfo busStationInfo = new BusStationInfo();
        //根据起始站和终点站获取车站信息，取第一条信息
        if (StringUtils.isNotEmpty(param.getOrigin()) && StringUtils.isNotEmpty(param.getStationName())) {
            BusStationInfo stationInfo = new BusStationInfo();
            stationInfo.setOrigin(param.getOrigin());
            stationInfo.setStationName(param.getStationName());
            List<BusStationInfo> stationInfoList = busStationInfoMapper.findBusStationList(stationInfo);
            if (CollectionUtils.isNotEmpty(stationInfoList)) {
                return stationInfoList.get(0);
            }
        }

        return busStationInfo;

    }

    private List<BusInsuranceInfo> findInsuranceInfo(BusOrderInfo param) {
        List<BusInsuranceInfo> busInsuranceInfoList = new ArrayList<BusInsuranceInfo>();
        if (param != null && (StringUtils.isNotEmpty(param.getOrdercode()) || StringUtils.isNotEmpty(param.getCardNo()))) {
            BusInsuranceInfo busInsuranceInfo = new BusInsuranceInfo();
            busInsuranceInfo.setOrdercode(StringUtils.isNotEmpty(param.getOrdercode()) ? param.getOrdercode() : null);
            busInsuranceInfo.setCardno(StringUtils.isNotEmpty(param.getCardNo()) ? param.getCardNo() : null);
            return busInsuranceInfoMapper.findBusInsuranceList(busInsuranceInfo);
        }

        return busInsuranceInfoList;
    }

    @PostMapping(value = "/getOriginalOrderJsonByUserId")
    public String getOriginalOrderJsonByUserId(@RequestBody BusOrderInfo busOrderInfo) throws Exception {

        if (busOrderInfo != null && StringUtils.isNotEmpty(busOrderInfo.getUserid())) {
            return busOrderService.getOriginalUserOrder(busOrderInfo);
        }
        return null;
    }

    @PostMapping(value = "/getBusOrderJsonByUserId")
    public List<BusOrderInfo> getBusOrderJsonByUserId(@RequestBody BusOrderInfo busOrderInfo) throws Exception {

        if (busOrderInfo != null && StringUtils.isNotEmpty(busOrderInfo.getUserid())) {
            return busOrderService.getUserOrder(busOrderInfo);
        }
        return null;
    }

    @PostMapping(value = "/fetchBusOrder")
    public void fetchBusOrder() throws Exception {
        BusOrderInfo param = new BusOrderInfo();
        param.setTimeRemark(busOrderService.getTimeHour());

        // 查询本地订单表数据
        List<BusOrderInfo> busOrderInfoList = busOrderInfoMapper.findBusOrderList(param);

        //查询接口结果
        BusOrderInfo result = null;
        //查询接口订单结果集合
        List<BusOrderInfo> resultList = null;
        //保险信息
        List<BusInsuranceInfo> busInsuranceInfoList = null;

        if (CollectionUtils.isNotEmpty(busOrderInfoList)) {
            resultList = new ArrayList<BusOrderInfo>();
            busInsuranceInfoList = new ArrayList<BusInsuranceInfo>();
            for (BusOrderInfo parmBusOrder : busOrderInfoList) {
                result = busOrderService.getBusOrder(parmBusOrder);
                if (result != null && !"404".equals(result.getErrcode()) && !"401".equals(result.getErrcode()) && !"400".equals
                        (result.getErrcode())) {
                    result.setMallOrderid(parmBusOrder.getMallOrderid());
                    if (CollectionUtils.isNotEmpty(result.getInsuranceInfo())) {
                        busInsuranceInfoList.addAll(this.buildInsurance(result.getInsuranceInfo(), parmBusOrder));
                    }
                    result.setUserid(parmBusOrder.getUserid());
                    resultList.add(result);
                }
            }
        }

        //新增数据
        if (CollectionUtils.isNotEmpty(resultList)) {
            oldBusOrderInfoMapper.addList(resultList);
        }

        //新增数据
        if (CollectionUtils.isNotEmpty(busInsuranceInfoList)) {
            busInsuranceInfoMapper.addList(busInsuranceInfoList);
        }
    }

    private List<BusInsuranceInfo> buildInsurance(List<BusInsuranceInfo> insuranceInfo, BusOrderInfo
            parmBusOrder) {

        if (CollectionUtils.isNotEmpty(insuranceInfo)) {
            for (BusInsuranceInfo param : insuranceInfo) {
                param.setOrdercode(parmBusOrder.getOrdercode());
            }
        }

        return insuranceInfo;
    }

    @PostMapping(value = "/fetchBusOrderByUserId")
    public String fetchBusOrderByUserId(@RequestBody BusOrderInfo busOrderInfo) throws IOException {

        return null;
    }


    @PostMapping(value = "/deleteAll")
    public void deleteAll() throws IOException {
        oldBusOrderInfoMapper.delete();
        busInsuranceInfoMapper.delete();
    }

    @PostMapping(value = "/deleteByUserId")
    public void deleteByUserId(@RequestBody BusOrderInfo busOrderInfo) throws IOException {

        if (busOrderInfo == null || StringUtils.isEmpty(busOrderInfo.getUserid())) {
            return;
        }

        BusInsuranceInfo busInsuranceInfo = new BusInsuranceInfo();
        busInsuranceInfo.setMobile(busOrderInfo.getMobile());

        oldBusOrderInfoMapper.deleteByMobile(busOrderInfo);
        busInsuranceInfoMapper.deleteByMobile(busInsuranceInfo);
    }

}