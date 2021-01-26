package com.xishan.store.trade.api.request;

import com.xishan.store.base.page.PageCommon;
import lombok.Data;

import java.util.Date;

@Data
public class PagingOrderRequest extends PageCommon {
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
