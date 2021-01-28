package com.xishan.store.trade.server.mq.listener;

import com.alibaba.fastjson.JSON;
import com.xishan.store.activity.api.enums.RecepitStatusEnum;
import com.xishan.store.activity.api.facade.RecepitWriteFacade;
import com.xishan.store.activity.api.request.RecepitUpdateRequest;
import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.OrderWriteFacade;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;
import com.xishan.store.trade.server.mq.message.CreateOrderMessage;
import com.xishan.store.trade.server.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateOrderListener  implements TopicListener{
    @Autowired
    private OrderWriteFacade orderWriteFacade;

    @Reference
    private RecepitWriteFacade recepitWriteFacade;

    @Override
    public void execute(String jsonBody) {
        if(jsonBody == null){
            log.info("消息为空");
        }
        CreateOrderMessage createOrderMessage = JSON.parseObject(jsonBody,CreateOrderMessage.class);
        CreateOrderRequest createOrderRequest = BeanUtil.convertToBean(createOrderMessage,CreateOrderRequest.class);
        Response<CreateOrderResponse> response = orderWriteFacade.createOrder(createOrderRequest);//创建订单成功的话，更新单据状态
        if(response.isSuccess()){
            RecepitUpdateRequest recepitUpdateRequest = new RecepitUpdateRequest();
            recepitUpdateRequest.setId(createOrderMessage.getRecepitId());
            recepitUpdateRequest.setStatus(RecepitStatusEnum.VALID.getValue());
            recepitUpdateRequest.setOrderId(response.getData().getOrderId());
            recepitWriteFacade.update(recepitUpdateRequest);
            //更新单据
        }else {
            RecepitUpdateRequest recepitUpdateRequest = new RecepitUpdateRequest();
            recepitUpdateRequest.setId(createOrderMessage.getRecepitId());
            recepitUpdateRequest.setStatus(RecepitStatusEnum.INVALID.getValue());
            recepitWriteFacade.update(recepitUpdateRequest);
            throw new ServiceException("创建订单失败"+response.getMessage());
        }
    }
}
