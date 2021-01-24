package com.xishan.store.trade.server.mapper;

import com.xishan.store.trade.api.model.OrderLine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderLineMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderLine record);

    int insertSelective(OrderLine record);

    OrderLine selectByPrimaryKey(Integer id);

    OrderLine selectByOrderId(Integer id);

    int updateByPrimaryKeySelective(OrderLine record);

    int updateByPrimaryKey(OrderLine record);
}