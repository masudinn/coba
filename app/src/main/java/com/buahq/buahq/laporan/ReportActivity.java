package com.buahq.buahq.laporan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.buahq.buahq.databinding.ActivityReportBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;
    private ReportAdapter adapter;
    private long from = 0, to = 0;
    private int price = 0;
    private int priceDiff = 0;
    NumberFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // tanggal mulai
        binding.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStartDate();
            }
        });

        // hingga tanggal
        binding.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEndDate();
            }
        });

        // tekan cari
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from == 0) {
                    Toast.makeText(ReportActivity.this, "Penjualan Pada Tanggal, harus mengandung tanggal", Toast.LENGTH_SHORT).show();
                    return;
                } else if (to == 0) {
                    Toast.makeText(ReportActivity.this, "Sampai Pada Tanggal, harus mengandung tanggal", Toast.LENGTH_SHORT).show();
                    return;
                }

                initRecyclerView();
                initViewModel();
            }
        });

    }

    private void initRecyclerView() {
        // tampilkan daftar product
        binding.rvReport.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter();
        binding.rvReport.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void initViewModel() {
        ReportViewModel viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListReport(from, to);
        viewModel.getReport().observe(this, model -> {
            if (model.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(model);

                for(int i=0; i<model.size(); i++) {
                    price += model.get(i).getPrice();
                    priceDiff += model.get(i).getPriceDiff();
                }

                binding.finalPrice.setText("Rp." + formatter.format(price));
                binding.priceDiff.setText("Rp." + formatter.format(priceDiff));
                priceDiff = 0;
                price = 0;

            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getStartDate() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Penjualan Pada Tanggal").build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            from = Long.parseLong(selection.toString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String format = sdf.format(new Date(Long.parseLong(selection.toString())));
            binding.startDate.setText(format);
        });
    }

    private void getEndDate() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Sampai Pada Tanggal").build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            to = Long.parseLong(selection.toString()) + (23*60*60*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String format = sdf.format(new Date(Long.parseLong(selection.toString())));
            binding.endDate.setText(format);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}