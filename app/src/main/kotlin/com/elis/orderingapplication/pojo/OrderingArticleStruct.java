
package com.elis.orderingapplication.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OrderingArticleStruct {

    @SerializedName("articleNo")
    @Expose
    private String articleNo;
    @SerializedName("articleDescription")
    @Expose
    private String articleDescription;
    @SerializedName("articleSize")
    @Expose
    private String articleSize;
    @SerializedName("articleTargetQty")
    @Expose
    private String articleTargetQty;
    @SerializedName("articleMinQty")
    @Expose
    private String articleMinQty;
    @SerializedName("articleMaxQty")
    @Expose
    private String articleMaxQty;
    @SerializedName("articleIntervalQty")
    @Expose
    private String articleIntervalQty;

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public String getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(String articleSize) {
        this.articleSize = articleSize;
    }

    public String getArticleTargetQty() {
        return articleTargetQty;
    }

    public void setArticleTargetQty(String articleTargetQty) {
        this.articleTargetQty = articleTargetQty;
    }

    public String getArticleMinQty() {
        return articleMinQty;
    }

    public void setArticleMinQty(String articleMinQty) {
        this.articleMinQty = articleMinQty;
    }

    public String getArticleMaxQty() {
        return articleMaxQty;
    }

    public void setArticleMaxQty(String articleMaxQty) {
        this.articleMaxQty = articleMaxQty;
    }

    public String getArticleIntervalQty() {
        return articleIntervalQty;
    }

    public void setArticleIntervalQty(String articleIntervalQty) {
        this.articleIntervalQty = articleIntervalQty;
    }

}
