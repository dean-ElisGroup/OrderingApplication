
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingDeliveryAddressStruct {

    @SerializedName("deliveryAddressNo")
    @Expose
    private String deliveryAddressNo;
    @SerializedName("deliveryAddressName")
    @Expose
    private String deliveryAddressName;
    @SerializedName("pointsOfService")
    @Expose
    private PointsOfService pointsOfService;

    public String getDeliveryAddressNo() {
        return deliveryAddressNo;
    }

    public void setDeliveryAddressNo(String deliveryAddressNo) {
        this.deliveryAddressNo = deliveryAddressNo;
    }

    public String getDeliveryAddressName() {
        return deliveryAddressName;
    }

    public void setDeliveryAddressName(String deliveryAddressName) {
        this.deliveryAddressName = deliveryAddressName;
    }

    public PointsOfService getPointsOfService() {
        return pointsOfService;
    }

    public void setPointsOfService(PointsOfService pointsOfService) {
        this.pointsOfService = pointsOfService;
    }

}
