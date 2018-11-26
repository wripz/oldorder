package com.ekold.mapper;

import com.ekold.requests.PaymentInfos;
import com.ekold.requests.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/2
 */
@Mapper     //声明是一个Mapper,与springbootApplication中的@MapperScan二选一写上即可
@Repository
public interface PayInfoMapper {

    void addList(@Param("list") List<PaymentInfos> param);

    List<PaymentInfos> findPayInfoList(@Param("param") PaymentInfos paymentInfos);

    void delete();

    void deleteByOrderId(@Param("param") UserInfo param);
}