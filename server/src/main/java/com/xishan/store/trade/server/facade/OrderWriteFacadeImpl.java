package com.xishan.store.trade.server.facade;

import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.OrderWriteFacade;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.request.UpdateOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;
import com.xishan.store.trade.server.service.OrderService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class OrderWriteFacadeImpl implements OrderWriteFacade {

    @Autowired
    private OrderService orderService;

    @Override
    public Response<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        try {
            CreateOrderResponse createOrderResponse =   orderService.createOrder(createOrderRequest);
            return Response.ok(createOrderResponse);
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<Integer> update(UpdateOrderRequest updateOrderRequest) {
        try {
            Integer n =  orderService.update(updateOrderRequest);
            return Response.ok(n);
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
}
