package com.xishan.store.trade.server;

import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.server.service.OrderService;
import com.xishan.store.usercenter.userapi.context.UserContext;
import com.xishan.store.usercenter.userapi.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServerStarterApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void contextLoads() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1l);
        userDTO.setUserName("xishan");
        UserContext.putCurrentUser(new UserDTO());
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGoodsId(1);
        createOrderRequest.setNum(1);
        createOrderRequest.setPayType((byte)1);
        createOrderRequest.setSkuId(1);
        createOrderRequest.setType((byte)1);
        orderService.createOrder(createOrderRequest);
    }



}
