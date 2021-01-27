package com.xishan.store.trade.server.service;

import com.xishan.store.base.exception.ServiceException;
import com.xishan.store.trade.api.enums.OrderStatusEnum;
import com.xishan.store.trade.api.model.AmountRecord;
import com.xishan.store.trade.api.model.Order;
import com.xishan.store.trade.api.model.UserAmount;
import com.xishan.store.trade.api.request.FindOrderRequest;
import com.xishan.store.trade.api.request.PayRequest;
import com.xishan.store.trade.api.request.UpdateOrderRequest;
import com.xishan.store.trade.api.response.OrderComplexDTO;
import com.xishan.store.trade.api.response.PayResponse;
import com.xishan.store.trade.server.mapper.AmountRecordMapper;
import com.xishan.store.trade.server.mapper.UserAmountMapper;
import com.xishan.store.usercenter.userapi.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 支付，不需要幂等，让订单用数据库乐观锁就ok
 */
@Service
public class PayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserAmountMapper userAmountMapper;

    @Autowired
    private AmountRecordMapper amountRecordMapper;

    public PayResponse pay(PayRequest payRequest){
        //查出order
        FindOrderRequest findOrderRequest = new  FindOrderRequest();
        findOrderRequest.setId(payRequest.getOrderId());
        OrderComplexDTO order = orderService.findById(findOrderRequest);
        if(order == null){
            throw new ServiceException("查找订单失败");
        }
        if(order.getStatus() != OrderStatusEnum.WAIT_PAY.getValue()){
            throw new ServiceException("订单状态非法:" +payRequest.getOrderId());
        }
        AmountRecord amountRecord = amountRecordMapper.selectByCode(payRequest.getBizCode());
        if(amountRecord != null){
            throw new ServiceException("不可重复付款");
        }
        Long userId = UserContext.getCurrentUser().getId();
        UserAmount userAmount = userAmountMapper.selectByUserId(userId);
        if(userAmount.getAmount() <  order.getAmount()){
            throw new ServiceException("余额不足");
        }
        //扣款 + 记录
        userAmount.setAmount(userAmount.getAmount() - order.getAmount());
        AmountRecord record = new AmountRecord();
        record.setAmount(order.getAmount());
        record.setCode(payRequest.getBizCode());
        record.setType(order.getType());
        record.setCreatedAt(new Date());
        record.setUserId(UserContext.getCurrentUser().getId());
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
        updateOrderRequest.setId(payRequest.getOrderId());
        updateOrderRequest.setAmount(order.getAmount());
        updateOrderRequest.setVersion(order.getVersion());

        orderService.update(updateOrderRequest);
        amountRecordMapper.insert(record);
        userAmountMapper.updateByPrimaryKeySelective(userAmount);
        //扣钱和记录
        PayResponse payResponse = new PayResponse();
        payResponse.setOrderId(order.getId());
        payResponse.setAmount(order.getAmount());
        return payResponse;
    }


}
