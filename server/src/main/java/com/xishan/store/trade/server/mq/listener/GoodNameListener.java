package com.xishan.store.trade.server.mq.listener;

import com.alibaba.fastjson.JSON;
import com.xishan.store.trade.api.model.OrderLine;
import com.xishan.store.trade.server.mapper.OrderLineMapper;
import com.xishan.store.trade.server.mq.message.GoodNameUpdateMessage;
import com.xishan.store.trade.server.service.OrderLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("goodName")
@Slf4j
public class GoodNameListener implements  TopicListener{

    @Autowired
    private OrderLineService orderLineService;

    @Override
    public void execute(String jsonBody) {
        if(jsonBody == null){
            log.info("消息为空");
        }
        GoodNameUpdateMessage goodNameUpdateMessage = JSON.parseObject(jsonBody,GoodNameUpdateMessage.class);

        OrderLine orderLine = new OrderLine();
        orderLine.setGoodsId(goodNameUpdateMessage.getId());
        orderLine.setGoodName(goodNameUpdateMessage.getGoodName());
        List<OrderLine> orderLines = orderLineService.findByCondition(orderLine);
        orderLines.forEach(it->{
            it.setGoodName(goodNameUpdateMessage.getGoodName());
            orderLineService.update(it);
        });

    }
}
