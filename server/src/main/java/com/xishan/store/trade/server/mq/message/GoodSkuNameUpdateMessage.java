package com.xishan.store.trade.server.mq.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodSkuNameUpdateMessage implements Serializable {

    private Integer id;

    private String skuName;
}
