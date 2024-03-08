
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderInfoResponse {

    @SerializedName("OrderInfoResult")
    @Expose
    private OrderInfoResult orderInfoResult;

    public OrderInfoResult getOrderInfoResult() {
        return orderInfoResult;
    }

    public void setOrderInfoResult(OrderInfoResult orderInfoResult) {
        this.orderInfoResult = orderInfoResult;
    }

}
