package com.buahq.buahq.bayar;

import android.os.Parcel;
import android.os.Parcelable;
import com.buahq.buahq.pesan.CheckoutModel;
import java.util.ArrayList;

public class PaymentModel implements Parcelable {

    private String transactionId;
    private int price;
    private String customerName;
    private String date;
    private String status;
    private int priceDiff;
    private int payment;
    public ArrayList<CheckoutModel> data;

    public PaymentModel() {}

    protected PaymentModel(Parcel in) {
        transactionId = in.readString();
        price = in.readInt();
        customerName = in.readString();
        date = in.readString();
        status = in.readString();
        priceDiff = in.readInt();
        payment = in.readInt();
        data = in.createTypedArrayList(CheckoutModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeInt(price);
        dest.writeString(customerName);
        dest.writeString(date);
        dest.writeString(status);
        dest.writeInt(priceDiff);
        dest.writeInt(payment);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(int priceDiff) {
        this.priceDiff = priceDiff;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public ArrayList<CheckoutModel> getData() {
        return data;
    }

    public void setData(ArrayList<CheckoutModel> data) {
        this.data = data;
    }
}
