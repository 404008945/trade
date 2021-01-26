package com.xishan.store.trade.api.facade;

import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.request.PayRequest;
import com.xishan.store.trade.api.response.PayResponse;

public interface PayFacade {
    Response<PayResponse> pay(PayRequest payRequest);
}
