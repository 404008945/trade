package com.xishan.store.trade.server.facade;

import com.xishan.store.base.page.Paging;
import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.facade.OrderReadFacade;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.request.PagingOrderRequest;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.server.service.OrderService;
import com.xishan.store.trade.server.util.BeanUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class OrderReadFacadeImpl implements OrderReadFacade {

    @Autowired
    private OrderService orderService;


    @Override
    public Response<OrderComplexDTO> findById(FindOrderRequest findOrderRequest) {
        try {
            OrderComplexDTO orderComplexDTO = orderService.findById(findOrderRequest);
            return Response.ok(orderComplexDTO);
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }

    @Override
    public Response<Paging<OrderComplexDTO>> paging(PagingOrderRequest pagingOrderRequest) {
        try {
            OrderComplexDTO orderComplexDTO = BeanUtil.convertToBean(pagingOrderRequest,OrderComplexDTO.class);
            Paging<OrderComplexDTO> paging = orderService.paging(orderComplexDTO,pagingOrderRequest.getPageNo(),pagingOrderRequest.getPageSize());
            return Response.ok(paging);
        }catch (Exception e){
            return Response.fail(e.getMessage());
        }
    }
}
