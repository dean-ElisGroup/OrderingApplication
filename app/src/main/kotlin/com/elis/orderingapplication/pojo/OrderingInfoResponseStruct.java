
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingInfoResponseStruct {

    @SerializedName("OrderInfoResponse")
    @Expose
    private OrderInfoResponse orderInfoResponse;

    public OrderInfoResponse getOrderInfoResponse() {
        return orderInfoResponse;
    }

    public void setOrderInfoResponse(OrderInfoResponse orderInfoResponse) {
        this.orderInfoResponse = orderInfoResponse;
    }

}
