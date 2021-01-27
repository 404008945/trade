package com.xishan.store.trade.server.mapper;

import com.xishan.store.trade.api.model.OrderLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderLineMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderLine record);

    int insertSelective(OrderLine record);

    OrderLine selectByPrimaryKey(Integer id);

    List<OrderLine> selectByCondition(OrderLine orderLine);

    OrderLine selectByOrderId(Integer id);

    int updateByPrimaryKeySelective(OrderLine record);

    int updateByPrimaryKey(OrderLine record);
}