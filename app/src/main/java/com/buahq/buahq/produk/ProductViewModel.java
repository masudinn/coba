package com.buahq.buahq.produk;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ProductModel>> listProduct = new MutableLiveData<>();
    final ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
    private static final String TAG = ProductViewModel.class.getSimpleName();

    public void setListProduct() {
        productModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("product")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel model = new ProductModel();
                                model.setDp("" + document.get("dp"));
                                model.setName("" + document.get("name"));
                                model.setPriceBase(Integer.parseInt("" + document.get("priceBase")));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));
                                model.setPriceFinal(Integer.parseInt("" + document.get("priceFinal")));
                                model.setProductId("" + document.get("productId"));
                                model.setTotalSelling(Integer.parseInt("" + document.get("totalSelling")));

                                productModelArrayList.add(model);
                            }
                            listProduct.postValue(productModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListProductByQuery(String query) {
        productModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("product")
                    .whereGreaterThanOrEqualTo("nameTemp", query)
                    .whereLessThanOrEqualTo("nameTemp", query + '\uf8ff')
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel model = new ProductModel();
                                model.setDp("" + document.get("dp"));
                                model.setName("" + document.get("name"));
                                model.setPriceBase(Integer.parseInt("" + document.get("priceBase")));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));
                                model.setPriceFinal(Integer.parseInt("" + document.get("priceFinal")));
                                model.setProductId("" + document.get("productId"));
                                model.setTotalSelling(Integer.parseInt("" + document.get("totalSelling")));

                                productModelArrayList.add(model);
                            }
                            listProduct.postValue(productModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LiveData<ArrayList<ProductModel>> getProductList() {
        return listProduct;
    }


}
