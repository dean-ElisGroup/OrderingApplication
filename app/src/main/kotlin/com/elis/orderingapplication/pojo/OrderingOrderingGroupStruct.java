
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingOrderingGroupStruct {

    @SerializedName("orderingGroupNo")
    @Expose
    private String orderingGroupNo;
    @SerializedName("orderingGroupDescription")
    @Expose
    private String orderingGroupDescription;

    public String getOrderingGroupNo() {
        return orderingGroupNo;
    }

    public void setOrderingGroupNo(String orderingGroupNo) {
        this.orderingGroupNo = orderingGroupNo;
    }

    public String getOrderingGroupDescription() {
        return orderingGroupDescription;
    }

    public void setOrderingGroupDescription(String orderingGroupDescription) {
        this.orderingGroupDescription = orderingGroupDescription;
    }

}
