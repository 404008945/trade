package com.xishan.store.trade.api.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateOrderResponse implements Serializable {

    private String orderCode;

    private Integer orderId;

}
