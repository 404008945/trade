package com.xishan.store.trade.api.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateOrderRequest {
    private Integer id;

    private String code;

    private Byte type;

    private Long customerId;

    private Long amount;

    private Byte paymentType;

    private Byte status;

    private Date createTime;

    private Integer version;
}
