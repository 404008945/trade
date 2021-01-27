package com.xishan.store.trade.server.facade;

import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.PayFacade;
import com.xishan.store.trade.api.request.PayRequest;
import com.xishan.store.trade.api.response.PayResponse;
import com.xishan.store.trade.server.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;

public class PayFacadeImpl implements PayFacade {

    @Autowired
    private PayService payService;

    @Override
    public Response<PayResponse> pay(PayRequest payRequest) {
        try {
            PayResponse response = payService.pay(payRequest);
            return Response.ok(response);
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
}