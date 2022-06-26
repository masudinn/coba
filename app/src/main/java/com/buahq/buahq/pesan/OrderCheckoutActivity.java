package com.buahq.buahq.pesan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.buahq.buahq.MainActivity;
import com.buahq.buahq.databinding.ActivityOrderCheckoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderCheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION_ID = "trid";
    private ActivityOrderCheckoutBinding binding;
    private CheckoutAdapter adapter;
    int subTotal = 0;
    int priceDiff = 0;
    NumberFormat formatter = new DecimalFormat("#,###");
    private final ArrayList<CheckoutModel> listCart = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecyclerView();
        initViewModel();

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.btnAddToTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

    }

    private void formValidation() {
        String name = binding.nameEt.getText().toString().trim();
        String trId = getIntent().getStringExtra(EXTRA_TRANSACTION_ID);

        if (name.isEmpty()) {
            Toast.makeText(OrderCheckoutActivity.this, "Nama Pelanggan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar3.setVisibility(View.VISIBLE);

        if(trId == null) {
            String transactionId = String.valueOf(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            Map<String, Object> transaction = new HashMap<>();
            transaction.put("transactionId", transactionId);
            transaction.put("price", subTotal);
            transaction.put("customerName", name);
            transaction.put("date", formattedDate);
            transaction.put("status", "PROSES");
            transaction.put("priceDiff", priceDiff);
            transaction.put("payment", 0);
            transaction.put("data", listCart);

            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .document(transactionId)
                    .set(transaction)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                for(int i=0; i<listCart.size(); i++){
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("cart")
                                            .document(listCart.get(i).getCartId())
                                            .update("transactionId", transactionId);
                                }

                                binding.progressBar3.setVisibility(View.GONE);
                                Toast.makeText(OrderCheckoutActivity.this, "Berhasil melakukan checkout transaksi!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(OrderCheckoutActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                binding.progressBar3.setVisibility(View.GONE);
                                Toast.makeText(OrderCheckoutActivity.this, "Gagal melakukan checkout transaksi!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("price", subTotal);
            transaction.put("priceDiff", priceDiff);
            transaction.put("data", listCart);

            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .document(trId)
                    .update(transaction)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.progressBar3.setVisibility(View.GONE);
                                Toast.makeText(OrderCheckoutActivity.this, "Berhasil melakukan checkout transaksi!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(OrderCheckoutActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                binding.progressBar3.setVisibility(View.GONE);
                                Toast.makeText(OrderCheckoutActivity.this, "Gagal melakukan checkout transaksi!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }




    }

    private void initRecyclerView() {
        // tampilkan daftar product
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutAdapter();
        binding.rvCart.setAdapter(adapter);
    }

    // inisiasi view model untuk menampilkan list produk
    @SuppressLint("SetTextI18n")
    private void initViewModel() {
        CheckoutViewModel viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(getIntent().getStringExtra(EXTRA_TRANSACTION_ID) == null) {
            viewModel.setListCart();
        } else {
            viewModel.setListCartByTransactionId(getIntent().getStringExtra(EXTRA_TRANSACTION_ID));
        }
        viewModel.getCartList().observe(this, productList -> {
            if (productList.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                adapter.setData(productList);

                for (int i = 0; i < productList.size(); i++) {
                    subTotal += productList.get(i).getPrice();
                    priceDiff += productList.get(i).getPriceDiff();
                }

                binding.price.setText("Rp." + formatter.format(subTotal));
                listCart.addAll(productList);
                binding.btnAddToTransaction.setVisibility(View.VISIBLE);

            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}