package com.ekold.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/5
 */
@Data
@Accessors(chain = true)
public class BusStationInfo implements Serializable {

    private Long id;

    private String origin;

    private String stationName;

    private String stationAddress;

    private String stationPhone;
}