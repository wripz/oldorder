package com.ekold.requests;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
public class PaymentInfos implements Serializable {

    private Long orderId;

    private Long amount;

    private String currency;

    private String currencyDesc;
}