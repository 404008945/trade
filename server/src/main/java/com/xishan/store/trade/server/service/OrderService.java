package com.xishan.store.trade.server.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.base.page.Paging;
import com.xishan.store.base.util.Response;
import com.xishan.store.escommon.page.EsPage;
import com.xishan.store.item.api.facade.GoodReadFacade;
import com.xishan.store.item.api.facade.GoodSkuReadFacade;
import com.xishan.store.item.api.facade.GoodSkuWriteFacade;
import com.xishan.store.item.api.request.BuySkuRequest;
import com.xishan.store.item.api.request.FindByGoodRequest;
import com.xishan.store.item.api.request.FindGoodSkuRequest;
import com.xishan.store.item.api.response.BuySkuResponse;
import com.xishan.store.item.api.response.FindGoodsSkuResponse;
import com.xishan.store.item.api.response.GoodComplexDTO;
import com.xishan.store.item.api.response.GoodsSkuDTO;
import com.xishan.store.trade.api.enums.OrderStatusEnum;
import com.xishan.store.trade.api.model.Order;
import com.xishan.store.trade.api.model.OrderLine;
import com.xishan.store.trade.api.request.CreateOrderRequest;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.request.ListOrderRequest;
import com.xishan.store.trade.api.request.UpdateOrderRequest;
import com.xishan.store.trade.api.response.CreateOrderResponse;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.server.es.client.OrderEsClient;
import com.xishan.store.trade.server.mapper.OrderLineMapper;
import com.xishan.store.trade.server.mapper.OrderMapper;
import com.xishan.store.trade.server.util.BeanUtil;
import com.xishan.store.usercenter.userapi.context.UserContext;
import com.xishan.store.usercenter.userapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
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
    private GoodReadFacade goodReadFacade;

    @Reference
    private GoodSkuWriteFacade goodSkuWriteFacade;


    /**
     * 下单操作 尝试扣库存—不成功返回错误 成功则创建订单,因为库存已经做了幂等
     * @return
     */
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest){
        if(createOrderRequest == null){
            throw new ServiceException("OrderService.createOrder.入参错误");
        }
        BuySkuRequest buySkuRequest = new BuySkuRequest();
        String code = createOrderRequest.getCode();
        buySkuRequest.setUuid(code);
        buySkuRequest.setSkuId(createOrderRequest.getSkuId());
        buySkuRequest.setNum(createOrderRequest.getNum());
        buySkuRequest.setGoodId(createOrderRequest.getGoodsId());
        Response<BuySkuResponse> response = goodSkuWriteFacade.buyGoods(buySkuRequest);
        if (!response.isSuccess() || response.getData() == null) {
            throw new ServiceException("下单失败" + response.getMessage());
        }
        //创建订单
        //利用这个code做好幂等
        ListOrderRequest listOrderRequest = new ListOrderRequest();
        listOrderRequest.setCode(code);
        List<Order> orders = orderMapper.selectByOrderRequest(listOrderRequest);
        if(!CollectionUtils.isEmpty(orders)){
            throw  new ServiceException("不可重复下单");
        }
        Order order = new Order();
        order.setAmount(response.getData().getAmount());
        order.setCode(code);
        order.setPaymentType(createOrderRequest.getPayType());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
        order.setType(createOrderRequest.getType());
        order.setCustomerId(createOrderRequest.getUserId());
        int n = orderMapper.insert(order);
        if (n <= 0) {
            throw new ServiceException("操作失败");
        }
        FindByGoodRequest findByGoodRequest = new FindByGoodRequest();
        findByGoodRequest.setId(createOrderRequest.getGoodsId());
        Response<GoodComplexDTO> res = goodReadFacade.findByGoodId(findByGoodRequest);
        if(!res.isSuccess()){
            throw new ServiceException("查找商品失败"+res.getMessage());
        }
        FindGoodSkuRequest findGoodSkuRequest = new FindGoodSkuRequest();
        findGoodSkuRequest.setId(createOrderRequest.getSkuId());
        Response<FindGoodsSkuResponse> goodsSkuDTORes = goodSkuReadFacade.findById(findGoodSkuRequest);
        if(!goodsSkuDTORes.isSuccess()){
            throw new ServiceException("查找sku失败"+goodsSkuDTORes.getMessage());
        }
        OrderLine orderLine = new OrderLine();
        orderLine.setGoodsId(createOrderRequest.getGoodsId());
        orderLine.setOrderId(order.getId());
        orderLine.setSkuId(createOrderRequest.getSkuId());
        orderLine.setUserId(createOrderRequest.getUserId());
        orderLine.setOrderId(order.getId());
        orderLine.setUserName(createOrderRequest.getUserName());
        orderLine.setGoodName(res.getData().getGoodsName());
        orderLine.setSkuName(goodsSkuDTORes.getData().getTitle());
        orderLineMapper.insert(orderLine);
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderCode(code);
        createOrderResponse.setOrderId(order.getId());
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
    public Integer update(UpdateOrderRequest updateOrderRequest){

        Order order = orderMapper.selectByPrimaryKey(updateOrderRequest.getId());
        if (order == null) {
            throw new ServiceException("查询不到订单：" + updateOrderRequest.getId());
        }
        updateOrderRequest.setVersion(order.getVersion());
        Order record = BeanUtil.convertToBean(updateOrderRequest, Order.class);
        int n = orderMapper.updateByPrimaryKeySelective(record);
        if (n <= 0) {
            throw new ServiceException("订单已经被更改状态，请重新操作");
        }
        return n;
    }


    /**
     * 订单分页
     */
    public Paging<OrderComplexDTO> paging(OrderComplexDTO complexDTO, int pageNo, int pageSize) {
        EsPage<OrderComplexDTO> esPage = orderEsClient.paging(complexDTO, pageNo, pageSize);
        if (esPage == null) {
            throw new ServiceException("查询失败");
        }
        Paging<OrderComplexDTO> paging = new Paging<>();
        paging.setData(esPage.getData());
        paging.setPageNo(esPage.getPageNo());
        paging.setPageSize(esPage.getPageSize());
        paging.setTotal(Integer.valueOf(String.valueOf(esPage.getTotal())));
        return paging;
    }

    public Integer expire(){//查出所有过期的然后设置状态
        ListOrderRequest listOrderRequest = new ListOrderRequest();
        listOrderRequest.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
        long currentTime = System.currentTimeMillis() - 30 * 60 * 1000;
        listOrderRequest.setStartTime(new Date(currentTime));
        List<Order> orders = orderMapper.selectByOrderRequest(listOrderRequest);
        orders.forEach(it->{
            it.setStatus(OrderStatusEnum.CANCAL.getValue());
        });
        log.info("共查找到{}条记录",orders.size());
        int n = 0;
        if (!CollectionUtils.isEmpty(orders)) {
            n = orderMapper.batchUpdate(orders);
        }
        log.info("共查处理{}条记录", n);
        return n;
    }










}
