package com.xishan.store.trade.web.controller;

import com.xishan.store.base.annoation.Authority;
import com.xishan.store.base.annoation.ResponseJsonFormat;
import com.xishan.store.base.exception.RestException;
import com.xishan.store.base.page.Paging;
import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.OrderReadFacade;
import com.xishan.store.trade.api.facade.OrderWriteFacade;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.request.PagingOrderRequest;
import com.xishan.store.trade.api.request.UpdateOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value="订单",tags={"订单操作接口"})
@RequestMapping("/order")
@Controller
public class OrderController {

    @Reference
    private OrderReadFacade orderReadFacade;

    @Reference
    private OrderWriteFacade orderWriteFacade;


    @PostMapping("/findById")
    @ResponseBody
    @ResponseJsonFormat
    @Authority
    OrderComplexDTO findById(FindOrderRequest findOrderRequest){
        Response<OrderComplexDTO> response = orderReadFacade.findById(findOrderRequest);
        if(!response.isSuccess()){
            throw new RestException("查询失败"+response.getMessage());
        }
        return response.getData();
    }


    @PostMapping("/paging")
    @ResponseBody
    @ResponseJsonFormat
    @Authority
    Paging<OrderComplexDTO> paging(PagingOrderRequest pagingOrderRequest){
        Response<Paging<OrderComplexDTO>> response = orderReadFacade.paging(pagingOrderRequest);
        if(!response.isSuccess()){
            throw new RestException("分页失败"+response.getMessage());
        }
        return response.getData();
    }

    @PostMapping("/createOrder")
    @ResponseBody
    @ResponseJsonFormat
    @Authority
    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest){
        Response<CreateOrderResponse> response = orderWriteFacade.createOrder(createOrderRequest);
        if(!response.isSuccess()){
            throw new RestException("创建订单失败"+response.getMessage());
        }
        return response.getData();
    }



}
