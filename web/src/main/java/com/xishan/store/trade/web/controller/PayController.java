package com.xishan.store.trade.web.controller;

import com.xishan.store.base.annoation.Authority;
import com.xishan.store.base.annoation.ResponseJsonFormat;
import com.xishan.store.base.exception.RestException;
import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.PayFacade;
import com.xishan.store.trade.api.request.PayRequest;
import com.xishan.store.trade.api.response.PayResponse;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Api(value="支付接口",tags={"支付"})
@RequestMapping("/pay")
@Controller
public class PayController {

    @Reference
    private PayFacade payFacade;

    @PostMapping("/")
    @ResponseBody
    @ResponseJsonFormat
    @Authority
    public PayResponse pay(PayRequest payRequest) {
        payRequest.setBizCode(UUID.randomUUID().toString());
        Response<PayResponse> response = payFacade.pay(payRequest);
        if (!response.isSuccess()) {
            throw new RestException("查询失败" + response.getMessage());
        }
        return response.getData();
    }


}
