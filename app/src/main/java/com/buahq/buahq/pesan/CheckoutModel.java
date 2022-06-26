package com.buahq.buahq.pesan;

import android.os.Parcel;
import android.os.Parcelable;

public class CheckoutModel implements Parcelable {

    private String name;
    private String dp;
    private String keterangan;
    private String cartId;
    private String productId;
    private String temperature;
    private int total;
    private int price;
    private int priceDiff;
    private String transactionId;

    public CheckoutModel() {}

    protected CheckoutModel(Parcel in) {
        name = in.readString();
        dp = in.readString();
        keterangan = in.readString();
        cartId = in.readString();
        productId = in.readString();
        temperature = in.readString();
        total = in.readInt();
        price = in.readInt();
        priceDiff = in.readInt();
        transactionId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(dp);
        dest.writeString(keterangan);
        dest.writeString(cartId);
        dest.writeString(productId);
        dest.writeString(temperature);
        dest.writeInt(total);
        dest.writeInt(price);
        dest.writeInt(priceDiff);
        dest.writeString(transactionId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CheckoutModel> CREATOR = new Creator<CheckoutModel>() {
        @Override
        public CheckoutModel createFromParcel(Parcel in) {
            return new CheckoutModel(in);
        }

        @Override
        public CheckoutModel[] newArray(int size) {
            return new CheckoutModel[size];
        }
    };

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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(int priceDiff) {
        this.priceDiff = priceDiff;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
