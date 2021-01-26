package com.xishan.store.trade.server.mapper;


import com.xishan.store.trade.api.model.UserAmount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAmountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAmount record);

    int insertSelective(UserAmount record);

    UserAmount selectByPrimaryKey(Integer id);

    UserAmount selectByUserId(Long id);

    int updateByPrimaryKeySelective(UserAmount record);

    int updateByPrimaryKey(UserAmount record);
}