package com.buahq.buahq.pesan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.buahq.buahq.databinding.ActivityOrderBinding;
import com.buahq.buahq.produk.ProductAdapter;
import com.buahq.buahq.produk.ProductAddActivity;
import com.buahq.buahq.produk.ProductViewModel;

public class OrderActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION_ID = "trid";
    private ActivityOrderBinding binding;
    private OrderAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel("all");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // cari produk
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    initRecyclerView();
                    initViewModel(editable.toString());
                } else {
                    initRecyclerView();
                    initViewModel("all");
                }
            }
        });

        // checkout
        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra(EXTRA_TRANSACTION_ID) != null) {
                    Intent intent = new Intent(OrderActivity.this, OrderCheckoutActivity.class);
                    intent.putExtra(OrderCheckoutActivity.EXTRA_TRANSACTION_ID, getIntent().getStringExtra(EXTRA_TRANSACTION_ID));
                    startActivity(intent);
                } else {
                    startActivity(new Intent(OrderActivity.this, OrderCheckoutActivity.class));
                }
            }
        });

    }


    private void initRecyclerView() {
        // tampilkan daftar product
        binding.rvProduct.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new OrderAdapter(getIntent().getStringExtra(EXTRA_TRANSACTION_ID));
        binding.rvProduct.setAdapter(adapter);
    }

    // inisiasi view model untuk menampilkan list produk
    private void initViewModel(String query) {
        ProductViewModel productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(query.equals("all")) {
            productViewModel.setListProduct();
        } else {
            productViewModel.setListProductByQuery(query);
        }
        productViewModel.getProductList().observe(this, productList -> {
            if (productList.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(productList);
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