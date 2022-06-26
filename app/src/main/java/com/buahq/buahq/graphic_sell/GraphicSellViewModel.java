package com.buahq.buahq.graphic_sell;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GraphicSellViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<GraphicSellModel>> listData = new MutableLiveData<>();
    final ArrayList<GraphicSellModel> graphicSellModelArrayList = new ArrayList<>();
    private static final String TAG = GraphicSellViewModel.class.getSimpleName();

    public void setGraphicSellList() {
        graphicSellModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("graphic_sell")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GraphicSellModel model = new GraphicSellModel();
                                model.setDate("" + document.get("date"));
                                model.setIncome(Integer.parseInt("" + document.get("income")));
                                model.setMonth("" + document.get("month"));
                                model.setUid("" + document.get("uid"));
                                model.setYear("" + document.get("year"));

                                graphicSellModelArrayList.add(model);
                            }
                            listData.postValue(graphicSellModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGraphicSellByYear(String year) {
        graphicSellModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("graphic_sell")
                    .whereEqualTo("year", year)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GraphicSellModel model = new GraphicSellModel();
                                model.setDate("" + document.get("date"));
                                model.setIncome(Integer.parseInt("" + document.get("income")));
                                model.setMonth("" + document.get("month"));
                                model.setUid("" + document.get("uid"));
                                model.setYear("" + document.get("year"));

                                graphicSellModelArrayList.add(model);
                            }
                            listData.postValue(graphicSellModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<ArrayList<GraphicSellModel>> getGraphicSell() {
        return listData;
    }


}
