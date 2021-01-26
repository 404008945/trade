package com.xishan.store.trade.api.response;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayResponse implements Serializable {

    private Integer orderId;

    private Long amount;
}
