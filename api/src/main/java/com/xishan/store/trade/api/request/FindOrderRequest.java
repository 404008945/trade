package com.xishan.store.trade.api.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class FindOrderRequest implements Serializable {
    private Integer id;

    private String code;

    private Byte type;

    private Long customerId;

    private Long amount;

    private Byte paymentType;

    private Byte status;

    private Long userId;

    private Integer skuId;

    private Integer goodsId;

    private String userName;

    private String goodName;

    private String skuName;

    private Date createTime;
}
