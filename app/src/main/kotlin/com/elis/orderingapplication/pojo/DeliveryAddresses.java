
package com.elis.orderingapplication.pojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class DeliveryAddresses {

    @SerializedName("OrderingDeliveryAddressStruct")
    @Expose
    private List<OrderingDeliveryAddressStruct> orderingDeliveryAddressStruct;

    public List<OrderingDeliveryAddressStruct> getOrderingDeliveryAddressStruct() {
        return orderingDeliveryAddressStruct;
    }

    public void setOrderingDeliveryAddressStruct(List<OrderingDeliveryAddressStruct> orderingDeliveryAddressStruct) {
        this.orderingDeliveryAddressStruct = orderingDeliveryAddressStruct;
    }

}
