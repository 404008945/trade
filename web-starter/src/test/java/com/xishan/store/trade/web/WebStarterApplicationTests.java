package com.xishan.store.trade.web;

import com.xishan.store.trade.web.controller.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebStarterApplicationTests {
    @Autowired
    private OrderController orderController;

    @Test
    void contextLoads() {
    }

}
