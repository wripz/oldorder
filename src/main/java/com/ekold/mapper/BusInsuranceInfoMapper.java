package com.ekold.mapper;

import com.ekold.requests.BusInsuranceInfo;
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
public interface BusInsuranceInfoMapper {

    void addList(@Param("list") List<BusInsuranceInfo> param);

    void delete();

    void deleteByMobile(@Param("param") BusInsuranceInfo param);

    List<BusInsuranceInfo> findBusInsuranceList(@Param("param") BusInsuranceInfo param);

}