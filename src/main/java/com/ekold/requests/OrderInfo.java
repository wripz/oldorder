package com.ekold.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
@Accessors(chain = true)
public class OrderInfo implements Serializable {

    private Long id;

    private String orderId;

    private String createTime;

    private Date createTimeDate;

    private Integer goodsType;

    private String invoiceContent;

    private String invoiceSubject;

    private Integer invoiceType;

    private Integer orderStatus;

    private Integer orderType;

    private String payDescription;

    private Integer payOnDelivery;

    private Integer payStatus;

    private String payTime;

    private Date payTimeDate;

    private String remark;

    private Long shopId;

    private String shopSubject;

    private String subject;

    private Long userId;

    private Long totalPayAmount;

    private Integer counts;

    private Long goodsId;

    private String goodsSubject;

    private Long payPrice;

    private Long goodsVersion;

    private List<PaymentInfos> paymentInfos;

    private List<GoodsInfos> goodsInfos;
    private Integer pageSize;
    private Integer startRow;
    private Integer pageNumber;
}