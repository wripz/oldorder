package com.ekold.mapper;

import com.ekold.requests.BusOrderInfo;
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
public interface OldBusOrderInfoMapper {

    void addList(@Param("list") List<BusOrderInfo> param);

    List<BusOrderInfo> findBusOrderList(@Param("param") BusOrderInfo busOrderInfo);

    void delete();

    void deleteByMobile(@Param("param") BusOrderInfo busOrderInfo);

}