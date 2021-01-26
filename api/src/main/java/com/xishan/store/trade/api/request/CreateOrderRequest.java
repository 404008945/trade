package com.xishan.store.trade.api.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateOrderRequest implements Serializable {

    private Integer goodsId;

    private Integer skuId;

    private Integer num;

    private Byte payType;

    private Byte type;


}
