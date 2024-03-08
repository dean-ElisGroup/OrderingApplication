
package com.elis.orderingapplication.pojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Orders {

    @SerializedName("OrderingOrderStruct")
    @Expose
    private List<OrderingOrderStruct> orderingOrderStruct;

    public List<OrderingOrderStruct> getOrderingOrderStruct() {
        return orderingOrderStruct;
    }

    public void setOrderingOrderStruct(List<OrderingOrderStruct> orderingOrderStruct) {
        this.orderingOrderStruct = orderingOrderStruct;
    }

}
