package com.ekold.mapper;

import com.ekold.requests.BusStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/2
 */
@Mapper
@Repository
public interface BusStationInfoMapper {

    List<BusStationInfo> findBusStationList(@Param("param") BusStationInfo param);

}