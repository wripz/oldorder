package com.ekold.requests;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
public class GoodsInfos implements Serializable {

    private Integer count;

    private Long goodsId;

    private String goodsSubject;

    private Long goodsVersion;

    private Long id;

    private Long payPrice;

    private Long payPriceNoTax;

    private Long priceFinal;

    private Long priceSum;
}