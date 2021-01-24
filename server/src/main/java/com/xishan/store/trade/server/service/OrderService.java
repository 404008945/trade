package com.xishan.store.trade.server.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.base.util.Response;
import com.xishan.store.item.api.facade.GoodSkuReadFacade;
import com.xishan.store.item.api.facade.GoodSkuWriteFacade;
import com.xishan.store.item.api.request.BuySkuRequest;
import com.xishan.store.item.api.response.BuySkuResponse;
import com.xishan.store.item.api.response.GoodComplexDTO;
import com.xishan.store.trade.api.enums.OrderStatusEnum;
import com.xishan.store.trade.api.model.Order;
import com.xishan.store.trade.api.model.OrderLine;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.server.es.client.OrderEsClient;
import com.xishan.store.trade.server.mapper.OrderLineMapper;
import com.xishan.store.trade.server.mapper.OrderMapper;
import com.xishan.store.trade.server.util.BeanUtil;
import com.xishan.store.usercenter.userapi.context.UserContext;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLineMapper orderLineMapper;

    @Autowired
    private OrderEsClient orderEsClient;

    @Reference
    private GoodSkuReadFacade goodSkuReadFacade;

    @Reference
    private GoodSkuWriteFacade goodSkuWriteFacade;


    /**
     * 下单操作 尝试扣库存—不成功返回错误 成功则创建订单,因为库存已经做了幂等和
     * @return
     */
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest){
        if(createOrderRequest == null){
            throw new ServiceException("OrderService.createOrder.入参错误");
        }
        BuySkuRequest buySkuRequest = new BuySkuRequest();
        String code = UUID.randomUUID().toString();
        buySkuRequest.setUuid(code);
        buySkuRequest.setSkuId(createOrderRequest.getSkuId());
        buySkuRequest.setNum(createOrderRequest.getNum());
        buySkuRequest.setGoodId(createOrderRequest.getGoodsId());
        Response<BuySkuResponse> response = goodSkuWriteFacade.buyGoods(buySkuRequest);
        if (!response.isSuccess() || response.getData() == null) {
            throw new ServiceException("下单失败" + response.getMessage());
        }
        //创建订单
        Order order = new Order();
        order.setAmount(response.getData().getAmount());
        order.setCode(code);
        order.setPaymentType(createOrderRequest.getPayType());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
        order.setType(createOrderRequest.getType());
        order.setCustomerId(UserContext.getCurrentUser().getId());
        int n = orderMapper.insert(order);
        if (n <= 0) {
            throw new ServiceException("操作失败");
        }
        OrderLine orderLine = new OrderLine();
        orderLine.setGoodsId(createOrderRequest.getGoodsId());
        orderLine.setOrderId(order.getId());
        orderLine.setSkuId(createOrderRequest.getSkuId());
        orderLine.setUserId(1l);
        orderLine.setOrderId(order.getId());
        orderLineMapper.insert(orderLine);
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderCode(code);

        OrderComplexDTO orderComplexDTO = toComplex(order,orderLine);
        orderEsClient.index(orderComplexDTO);//新增的，加进去
        return createOrderResponse;
    }

    /**
     * es聚合 ，order和order_line数据聚合在一起
     */
    public PageInfo<Integer> pagingIds(int pageNo,int pageSize){
        PageHelper.startPage(pageNo,pageSize);//利用了mybatis的拦截器
        List<Integer> ids = orderMapper.pagingIds();
        PageInfo<Integer> pageInfo = new PageInfo(ids);
        return pageInfo;
    }


    /**
     * 查询order和order_line聚合数据
     */
    public OrderComplexDTO findById(FindOrderRequest findOrderRequest){
        //优先从es中获取，
        if (findOrderRequest == null) {
            throw new ServiceException("findOrderRequest不可为没空");
        }
        OrderComplexDTO req = new OrderComplexDTO();
        req.setId(findOrderRequest.getId());
        OrderComplexDTO  orderComplexDTO = orderEsClient.getById(req);
        if(orderComplexDTO != null){
            return orderComplexDTO;
        }
        //先从es中查 因为es是查询好的
        Order order = orderMapper.selectByPrimaryKey(findOrderRequest.getId());
        OrderLine orderLine = orderLineMapper.selectByOrderId(findOrderRequest.getId());
        return toComplex(order,orderLine);
    }

    public OrderComplexDTO toComplex(Order order,OrderLine orderLine){
        OrderComplexDTO orderComplexDTO = BeanUtil.convertToBean(order, OrderComplexDTO.class);
        orderComplexDTO.setUserId(orderLine.getUserId());
        orderComplexDTO.setGoodsId(orderLine.getGoodsId());
        orderComplexDTO.setSkuId(orderLine.getSkuId());
        orderComplexDTO.setUserName(orderLine.getUserName());
        orderComplexDTO.setGoodName(orderLine.getGoodName());
        orderComplexDTO.setSkuName(orderComplexDTO.getSkuName());
        return orderComplexDTO;
    }

    /**
     * 什么时候刷es
     */





}
