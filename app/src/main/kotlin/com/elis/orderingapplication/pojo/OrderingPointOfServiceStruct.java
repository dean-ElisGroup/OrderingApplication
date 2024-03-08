
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingPointOfServiceStruct {

    @SerializedName("pointOfServiceNo")
    @Expose
    private String pointOfServiceNo;
    @SerializedName("pointOfServiceName")
    @Expose
    private String pointOfServiceName;
    @SerializedName("pointOfServiceDescription")
    @Expose
    private String pointOfServiceDescription;
    @SerializedName("pointOfServiceOrderingGroupNo")
    @Expose
    private String pointOfServiceOrderingGroupNo;
    @SerializedName("orders")
    @Expose
    private Orders orders;

    public String getPointOfServiceNo() {
        return pointOfServiceNo;
    }

    public void setPointOfServiceNo(String pointOfServiceNo) {
        this.pointOfServiceNo = pointOfServiceNo;
    }

    public String getPointOfServiceName() {
        return pointOfServiceName;
    }

    public void setPointOfServiceName(String pointOfServiceName) {
        this.pointOfServiceName = pointOfServiceName;
    }

    public String getPointOfServiceDescription() {
        return pointOfServiceDescription;
    }

    public void setPointOfServiceDescription(String pointOfServiceDescription) {
        this.pointOfServiceDescription = pointOfServiceDescription;
    }

    public String getPointOfServiceOrderingGroupNo() {
        return pointOfServiceOrderingGroupNo;
    }

    public void setPointOfServiceOrderingGroupNo(String pointOfServiceOrderingGroupNo) {
        this.pointOfServiceOrderingGroupNo = pointOfServiceOrderingGroupNo;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

}
