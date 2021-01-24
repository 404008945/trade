package com.xishan.store.trade.api.response;

import lombok.Data;

import java.util.Date;

/**
 * order信息聚合
 */
@Data
public class OrderComplexDTO {

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
