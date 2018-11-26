package com.ekold.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
@Accessors(chain = true)
public class BusInsuranceInfo implements Serializable {

    private String cardno;

    private String mobile;

    private String name;

    private String ordercode;

    private BigDecimal price;

    private String status;

    private String policyno;

    private String company;

    private Date termdate;

    private String termdateStr;

    private Date fromdate;

    private String fromdateStr;

    private Date time;

    private String timeStr;
}