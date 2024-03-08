
package com.elis.orderingapplication.pojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class PointsOfService {

    @SerializedName("OrderingPointOfServiceStruct")
    @Expose
    private List<OrderingPointOfServiceStruct> orderingPointOfServiceStruct;

    public List<OrderingPointOfServiceStruct> getOrderingPointOfServiceStruct() {
        return orderingPointOfServiceStruct;
    }

    public void setOrderingPointOfServiceStruct(List<OrderingPointOfServiceStruct> orderingPointOfServiceStruct) {
        this.orderingPointOfServiceStruct = orderingPointOfServiceStruct;
    }

}
