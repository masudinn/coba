package com.buahq.buahq.bayar;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<PaymentModel>> listTransaction = new MutableLiveData<>();
    final ArrayList<PaymentModel> transactionPaymentModelArrayList = new ArrayList<>();
    private static final String TAG = PaymentViewModel.class.getSimpleName();

    public void setTransactionList() {
        transactionPaymentModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .orderBy("status", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PaymentModel model = new PaymentModel();
                                model.setCustomerName("" + document.get("customerName"));
                                model.setDate("" + document.get("date"));
                                model.setPrice(Integer.parseInt("" + document.get("price")));
                                model.setStatus("" + document.get("status"));
                                model.setTransactionId("" + document.get("transactionId"));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));
                                model.setPayment(Integer.parseInt("" + document.get("payment")));
                                model.setData(document.toObject(PaymentModel.class).data);

                                transactionPaymentModelArrayList.add(model);
                            }
                            listTransaction.postValue(transactionPaymentModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<ArrayList<PaymentModel>> getTransaction() {
        return listTransaction;
    }


}
