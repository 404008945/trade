package com.xishan.store.trade.server.mapper;


import com.xishan.store.trade.api.model.Order;
import com.xishan.store.trade.api.request.ListOrderRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    List<Order> selectByOrderRequest(ListOrderRequest listOrderRequest);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    int batchUpdate(List<Order> record);

    List<Integer> pagingIds();
}