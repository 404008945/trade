package com.xishan.store.trade.server;

import com.xishan.store.item.api.facade.GoodSkuWriteFacade;
import com.xishan.store.item.api.request.BuySkuRequest;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.server.service.OrderService;
import com.xishan.store.usercenter.userapi.context.UserContext;
import com.xishan.store.usercenter.userapi.dto.UserDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static java.lang.Thread.sleep;

@SpringBootTest
class ServerStarterApplicationTests {

    @Autowired
    private OrderService orderService;

    @Reference
    private GoodSkuWriteFacade goodSkuWriteFacade;

    @Test
    void contextLoads() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1l);
        userDTO.setUserName("xishan");
        UserContext.putCurrentUser(userDTO);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGoodsId(1);
        createOrderRequest.setNum(1);
        createOrderRequest.setPayType((byte)1);
        createOrderRequest.setSkuId(1);
        createOrderRequest.setType((byte)1);
        orderService.createOrder(createOrderRequest);
    }

    @Test
    void test(){
        BuySkuRequest buySkuRequest = new  BuySkuRequest();
        buySkuRequest.setGoodId(1);
        buySkuRequest.setNum(1);
        buySkuRequest.setSkuId(1);
        buySkuRequest.setUuid(UUID.randomUUID().toString());
        goodSkuWriteFacade.buyGoods(buySkuRequest);
        while (true){
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





}
