package com.ekold.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
@Accessors(chain = true)
public class BusOrderInfo implements Serializable {

    private Long mallOrderid;

    private Long orderid;

    private String createTime;

    private Date createTimeDate;

    private String ordercode;

    private String classCode;

    private String stationCode;

    private BigDecimal amount;

    private String tickets;

    private String origin;

    private String mobile;

    private String bookTimeStr;

    private Date bookTime;

    private String className;

    private String cardNo;

    private String classTimeStr;

    private Date classTime;

    private String password;

    private String site;

    private BigDecimal price;

    private String name;

    private String stationName;

    private String seatNo;

    private String status;

    private String timeRemark;

    private String userid;

    private String errcode;

    private List<BusInsuranceInfo> insuranceInfo;

    private BusStationInfo busStationInfo;

    private Integer pageSize;
    private Integer startRow;
    private Integer pageNumber;

}