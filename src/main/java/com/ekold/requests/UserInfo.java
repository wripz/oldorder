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
public class UserInfo implements Serializable {

    private int id;

    private String userId;

    private String city;

    private String nickName;

    private String headerImage;

    private Long memberId;

    private String version;            //App客户端的版本号

    private Integer osType;                //操作系统类型  1标识安卓  2标识IOS

    private String who;

    private Long orderId;

    //标记日期，取这个日期之前半年的数据
    private String dateRemark;

    private Integer pageSize;
    private Integer startRow;
    private Integer pageNumber;
}