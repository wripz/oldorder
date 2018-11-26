package com.ekold.mapper;

import com.ekold.requests.OrderInfo;
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
public interface OrderInfoMapper {

    void addList(@Param("list") List<OrderInfo> param);

    List<OrderInfo> findOrderList(@Param("param") OrderInfo orderInfo);

    void delete();

    void deleteByUserId(@Param("param") UserInfo userInfo);

}