package com.buahq.buahq.bayar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.buahq.buahq.R;
import com.buahq.buahq.databinding.ActivityPaymentDetailBinding;
import com.buahq.buahq.pesan.CheckoutAdapter;
import com.buahq.buahq.pesan.OrderActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT = "payment";
    private ActivityPaymentDetailBinding binding;
    private PaymentModel model;
    private int payment;
    private int totalProduct;
    private String produkTerlaris;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_PAYMENT);
        NumberFormat formatter = new DecimalFormat("#,###");

        Log.e("TAG", model.toString());

        binding.nameEt.setText(model.getCustomerName());
        binding.price.setText("Rp." + formatter.format(model.getPrice()));


        if (model.getStatus().equals("PROSES")) {
            binding.finishBtn.setVisibility(View.VISIBLE);
            binding.fabAdd.setEnabled(true);
        } else {
            binding.textInputLayout2.setEnabled(false);
            binding.paymentEt.setText("" + model.getPayment());
            binding.kembalian.setText("Kembalian Rp." + formatter.format(model.getPayment() - model.getPrice()));
        }

        initRecyclerView();

        binding.paymentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    payment = Integer.parseInt(editable.toString());
                    if (Integer.parseInt(editable.toString()) < model.getPrice()) {
                        binding.kembalian.setText("Pembayaran tidak mencukupi");
                    } else {
                        binding.kembalian.setText("Kembalian Rp." + formatter.format(payment - model.getPrice()));
                    }
                }
            }
        });


        /// bayar minuman
        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

        /// tambah produk
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentDetailActivity.this, OrderActivity.class);
                intent.putExtra(OrderActivity.EXTRA_TRANSACTION_ID, model.getTransactionId());
                startActivity(intent);
            }
        });

    }

    private void formValidation() {
        if (payment < model.getPrice()) {
            Toast.makeText(PaymentDetailActivity.this, "Pembayaran tidak cukup", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar3.setVisibility(View.VISIBLE);

        String reportId = String.valueOf(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c.getTime());

        DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        Date date = new Date();
        String month = dateFormat.format(date);



        /// ambil barang terlaris
        int tot = 0;
        for (int i = 0; i < model.getData().size(); i++) {
            totalProduct += model.getData().get(i).getTotal();

            if (tot < model.getData().get(i).getTotal()) {
                tot = model.getData().get(i).getTotal();
                produkTerlaris = model.getData().get(i).getName();
            }
        }

        Map<String, Object> report = new HashMap<>();

        report.put("reportId", reportId);
        report.put("date", formattedDate);
        report.put("price", model.getPrice());
        report.put("priceDiff", model.getPriceDiff());
        report.put("total", totalProduct);
        report.put("terlaris", produkTerlaris);
        report.put("timeInMillis", Long.parseLong(reportId));

        // buat report
        FirebaseFirestore
                .getInstance()
                .collection("report")
                .document(reportId)
                .set(report)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // update status transaksi
                        Map<String, Object> transaction = new HashMap<>();
                        transaction.put("status", "SUKSES");
                        transaction.put("payment", payment);

                        FirebaseFirestore
                                .getInstance()
                                .collection("transaction")
                                .document(model.getTransactionId())
                                .update(transaction);


                        // grafik penjualan
                        Map<String, Object> graphic = new HashMap<>();
                        graphic.put("uid", reportId);
                        graphic.put("date", formattedDate);
                        graphic.put("month", month);
                        graphic.put("year", formattedDate.substring(formattedDate.length()-4));
                        graphic.put("income", model.getPrice());

                        FirebaseFirestore
                                .getInstance()
                                .collection("graphic_sell")
                                .document(reportId)
                                .set(graphic);


                        // update produk terjual
                        for (int i = 0; i < model.getData().size(); i++) {

                            Map<String, Object> produkTerjual = new HashMap<>();
                            produkTerjual.put("productId", model.getData().get(i).getProductId());
                            produkTerjual.put("qty", model.getData().get(i).getTotal());

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("produk_terjual")
                                    .document()
                                    .set(produkTerjual);

                        }

                        /// hapus cart
                        for (int i = 0; i < model.getData().size(); i++) {
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("cart")
                                    .document(model.getData().get(i).getCartId())
                                    .delete();
                        }

                        binding.progressBar3.setVisibility(View.GONE);
                        showSuccessDialog();


                    } else {
                        binding.progressBar3.setVisibility(View.GONE);
                        Toast.makeText(PaymentDetailActivity.this, "Gagal melakukan pembayaran", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Menyelesaikan Pembayaran")
                .setMessage("Transaksi Sukses")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void initRecyclerView() {
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        CheckoutAdapter adapter = new CheckoutAdapter();
        binding.rvCart.setAdapter(adapter);
        adapter.setData(model.getData());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}