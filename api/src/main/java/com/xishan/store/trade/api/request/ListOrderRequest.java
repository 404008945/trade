package com.xishan.store.trade.api.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ListOrderRequest implements Serializable {
    private String code;

    private Byte type;

    private Long customerId;

    private Long amount;

    private Byte paymentType;

    private Byte status;

    private Date startTime;

    private Date endTime;


}
