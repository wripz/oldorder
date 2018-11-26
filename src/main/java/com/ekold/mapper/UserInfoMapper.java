package com.ekold.mapper;

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
public interface UserInfoMapper {

    List<UserInfo> getUserInfo(@Param("param") UserInfo param);

    Integer getUserCount(@Param("param") UserInfo param);
}