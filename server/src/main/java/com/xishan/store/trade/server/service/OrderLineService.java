package com.xishan.store.trade.server.service;

import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.trade.api.model.OrderLine;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.server.es.client.OrderEsClient;
import com.xishan.store.trade.server.mapper.OrderLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderLineService {

    @Autowired
    private OrderLineMapper orderLineMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderEsClient esClient;

    public Integer update(OrderLine orderLine){
        //更新数据并更新至es
        int n = orderLineMapper.updateByPrimaryKey(orderLine);
        if(n<=0) {
            throw new ServiceException("更新失败");
        }
        //重新加入es
        OrderComplexDTO orderComplexDTO = new OrderComplexDTO();
        orderComplexDTO.setId(orderLine.getOrderId());
        esClient.deleteById(orderComplexDTO);
        FindOrderRequest findOrderRequest = new FindOrderRequest();
        findOrderRequest.setId(orderLine.getOrderId());
        orderComplexDTO = orderService.findById(findOrderRequest);
        esClient.index(orderComplexDTO);
        return n;
    }

    public List<OrderLine> findByCondition(OrderLine orderLine){
        //更新数据并更新至es
        return orderLineMapper.selectByCondition(orderLine);
    }
}
