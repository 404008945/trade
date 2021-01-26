package com.xishan.store.trade.api.facade;

import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.request.UpdateOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;

public interface OrderWriteFacade {

    Response<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest);

    Response<Integer> update(UpdateOrderRequest updateOrderRequest);
}
