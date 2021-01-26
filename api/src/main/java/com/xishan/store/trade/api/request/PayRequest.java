package com.xishan.store.trade.api.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayRequest implements Serializable {
    private Integer orderId;//订单id

    private String bizCode;// 用户幂等
}
