package com.buahq.buahq.pesan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CheckoutViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<CheckoutModel>> listCart = new MutableLiveData<>();
    final ArrayList<CheckoutModel> checkoutModelArrayList = new ArrayList<>();
    private static final String TAG = CheckoutViewModel.class.getSimpleName();

    public void setListCart() {
        checkoutModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .whereEqualTo("transactionId", "")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CheckoutModel model = new CheckoutModel();
                                model.setDp("" + document.get("dp"));
                                model.setName("" + document.get("name"));
                                model.setCartId("" + document.get("cartId"));
                                model.setProductId("" + document.get("productId"));
                                model.setKeterangan("" + document.get("keterangan"));
                                model.setPrice(Integer.parseInt("" + document.get("price")));
                                model.setTemperature("" + document.get("temperature"));
                                model.setTotal(Integer.parseInt("" + document.get("total")));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));

                                checkoutModelArrayList.add(model);
                            }
                            listCart.postValue(checkoutModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListCartByTransactionId(String transactionId) {
        checkoutModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .whereEqualTo("transactionId", transactionId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CheckoutModel model = new CheckoutModel();
                                model.setDp("" + document.get("dp"));
                                model.setName("" + document.get("name"));
                                model.setCartId("" + document.get("cartId"));
                                model.setProductId("" + document.get("productId"));
                                model.setKeterangan("" + document.get("keterangan"));
                                model.setPrice(Integer.parseInt("" + document.get("price")));
                                model.setTemperature("" + document.get("temperature"));
                                model.setTotal(Integer.parseInt("" + document.get("total")));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));

                                checkoutModelArrayList.add(model);
                            }
                            listCart.postValue(checkoutModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<ArrayList<CheckoutModel>> getCartList() {
        return listCart;
    }

}
