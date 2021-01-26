package com.xishan.store.trade.server.task;

import com.xishan.store.trade.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderExpireTask {

    @Autowired
    private OrderService orderService;

    //30分钟到期
    @Value("${order.expireTime:30}")
    private Integer expireTime;

    @Scheduled(fixedRate = 1000)
    public void orderTasks() {//查出所有待付款 ，过期的，然后过期
        orderService.expire();
    }
}
