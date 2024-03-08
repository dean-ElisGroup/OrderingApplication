
package com.elis.orderingapplication.pojo;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Articles {

    @SerializedName("OrderingArticleStruct")
    @Expose
    private List<OrderingArticleStruct> orderingArticleStruct;

    public List<OrderingArticleStruct> getOrderingArticleStruct() {
        return orderingArticleStruct;
    }

    public void setOrderingArticleStruct(List<OrderingArticleStruct> orderingArticleStruct) {
        this.orderingArticleStruct = orderingArticleStruct;
    }

}
