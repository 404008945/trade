package com.xishan.store.trade.api.enums;

public enum  OrderStatusEnum {

    WAIT_PAY(0),WAIT_ARRIVED(1),CANCAL(2),FINISH(2);

    private Byte value;

    private OrderStatusEnum(int value){
        this.value= (byte)value;
    }
    public  Byte  getValue(){
        return this.value;
    }

}
