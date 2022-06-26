package com.buahq.buahq.laporan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ReportModel>> listReport = new MutableLiveData<>();
    final ArrayList<ReportModel> reportModelArrayList = new ArrayList<>();
    private static final String TAG = ReportViewModel.class.getSimpleName();

    public void setListReport(long from, long to) {
        reportModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("report")
                    .whereGreaterThanOrEqualTo("timeInMillis", from)
                    .whereLessThanOrEqualTo("timeInMillis", to)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReportModel model = new ReportModel();
                                model.setDate("" + document.get("date"));
                                model.setPrice(Integer.parseInt("" + document.get("price")));
                                model.setPriceDiff(Integer.parseInt("" + document.get("priceDiff")));
                                model.setReportId("" + document.get("reportId"));
                                model.setTerlaris("" + document.get("terlaris"));
                                model.setTotal(Integer.parseInt("" + document.get("total")));

                                reportModelArrayList.add(model);
                            }
                            listReport.postValue(reportModelArrayList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<ArrayList<ReportModel>> getReport() {
        return listReport;
    }

}
