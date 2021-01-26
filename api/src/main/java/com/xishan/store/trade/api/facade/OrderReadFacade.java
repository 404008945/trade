package com.xishan.store.trade.api.facade;

import com.xishan.store.base.page.Paging;
import com.xishan.store.base.util.Response;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.request.PagingOrderRequest;
import com.xishan.store.trade.api.response.OrderComplexDTO;


public interface OrderReadFacade {

    Response<OrderComplexDTO> findById(FindOrderRequest findOrderRequest);

    Response<Paging<OrderComplexDTO>> paging(PagingOrderRequest pagingOrderRequest);
}
