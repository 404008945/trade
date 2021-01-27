package com.xishan.store.trade.server.mq.listener;

import com.alibaba.fastjson.JSON;
import com.xishan.store.trade.api.model.OrderLine;
import com.xishan.store.trade.server.mq.message.GoodSkuNameUpdateMessage;
import com.xishan.store.trade.server.service.OrderLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("skuTag")
@Slf4j
public class SkuNameListener  implements  TopicListener{

    @Autowired
    private OrderLineService orderLineService;

    @Override
    public void execute(String jsonBody) {
        if(jsonBody == null){
            log.info("消息为空");
        }
        GoodSkuNameUpdateMessage goodNameUpdateMessage = JSON.parseObject(jsonBody,GoodSkuNameUpdateMessage.class);

        OrderLine orderLine = new OrderLine();
        orderLine.setSkuId(goodNameUpdateMessage.getId());
        orderLine.setGoodName(goodNameUpdateMessage.getSkuName());
        List<OrderLine> orderLines = orderLineService.findByCondition(orderLine);
        orderLines.forEach(it->{
            it.setSkuName(goodNameUpdateMessage.getSkuName());
            orderLineService.update(it);
        });

    }
}
