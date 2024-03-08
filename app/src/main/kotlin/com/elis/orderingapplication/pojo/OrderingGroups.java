
package com.elis.orderingapplication.pojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingGroups {

    @SerializedName("OrderingOrderingGroupStruct")
    @Expose
    private List<OrderingOrderingGroupStruct> orderingOrderingGroupStruct;

    public List<OrderingOrderingGroupStruct> getOrderingOrderingGroupStruct() {
        return orderingOrderingGroupStruct;
    }

    public void setOrderingOrderingGroupStruct(List<OrderingOrderingGroupStruct> orderingOrderingGroupStruct) {
        this.orderingOrderingGroupStruct = orderingOrderingGroupStruct;
    }

}
