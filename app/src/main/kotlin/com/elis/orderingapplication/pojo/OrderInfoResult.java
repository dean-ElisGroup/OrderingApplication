
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderInfoResult {

    @SerializedName("deliveryAddresses")
    @Expose
    private DeliveryAddresses deliveryAddresses;
    @SerializedName("orderingGroups")
    @Expose
    private OrderingGroups orderingGroups;

    public DeliveryAddresses getDeliveryAddresses() {
        return deliveryAddresses;
    }

    public void setDeliveryAddresses(DeliveryAddresses deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public OrderingGroups getOrderingGroups() {
        return orderingGroups;
    }

    public void setOrderingGroups(OrderingGroups orderingGroups) {
        this.orderingGroups = orderingGroups;
    }

}
