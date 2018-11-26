package com.ekold.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/2
 */
@Data
@Accessors(chain = true)
public class Order implements Serializable {

    private Long goodsId;

    private String goodsSubject;

    private Long payPrice;

    private Long payPriceNoTax;

    private Long priceFinal;

    private Long priceSum;

    private Integer count;

    private String payDescription;

    private String invoiceContent;

    private String invoiceSubject;

    private Long amount;

    private String currency;

    private Long shopId;

    private String shopSubject;

    private Long memberId;

    private Long totalPayAmount;

    private String createTime;
}