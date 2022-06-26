    package com.buahq.buahq.bayar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import com.buahq.buahq.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private PaymentAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initRecyclerView() {
        // tampilkan daftar product
        binding.rvPayment.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter();
        binding.rvPayment.setAdapter(adapter);
    }

    // inisiasi view model untuk menampilkan list produk
    @SuppressLint("SetTextI18n")
    private void initViewModel() {
        PaymentViewModel viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setTransactionList();
        viewModel.getTransaction().observe(this, model -> {
            if (model.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(model);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}