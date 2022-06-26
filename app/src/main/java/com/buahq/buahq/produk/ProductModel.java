package com.buahq.buahq.produk;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductModel implements Parcelable {

    private String productId;
    private String name;
    private String dp;
    private int priceBase;
    private int priceFinal;
    private int priceDiff;
    private int totalSelling;

    public ProductModel(){}

    protected ProductModel(Parcel in) {
        productId = in.readString();
        name = in.readString();
        dp = in.readString();
        priceBase = in.readInt();
        priceFinal = in.readInt();
        priceDiff = in.readInt();
        totalSelling = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(dp);
        dest.writeInt(priceBase);
        dest.writeInt(priceFinal);
        dest.writeInt(priceDiff);
        dest.writeInt(totalSelling);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public int getPriceBase() {
        return priceBase;
    }

    public void setPriceBase(int priceBase) {
        this.priceBase = priceBase;
    }

    public int getPriceFinal() {
        return priceFinal;
    }

    public void setPriceFinal(int priceFinal) {
        this.priceFinal = priceFinal;
    }

    public int getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(int priceDiff) {
        this.priceDiff = priceDiff;
    }

    public int getTotalSelling() {
        return totalSelling;
    }

    public void setTotalSelling(int totalSelling) {
        this.totalSelling = totalSelling;
    }
}
