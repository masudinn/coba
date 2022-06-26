package com.buahq.buahq;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buahq.buahq.bayar.PaymentActivity;
import com.buahq.buahq.databinding.ActivityMainBinding;
import com.buahq.buahq.graphic_sell.GraphicSellActivity;
import com.buahq.buahq.laporan.ReportActivity;
import com.buahq.buahq.pesan.OrderActivity;
import com.buahq.buahq.produk.ProductActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    NumberFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onResume() {
        super.onResume();

        // cek pendapatan hari ini
        getProfitToday();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.produkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProductActivity.class));
            }
        });

        binding.pesanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
            }
        });

        binding.bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PaymentActivity.class));
            }
        });

        binding.laporanJualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
            }
        });

        binding.graphicSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GraphicSellActivity.class));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getProfitToday() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = df.format(c.getTime());

        FirebaseFirestore
                .getInstance()
                .collection("report")
                .whereEqualTo("date", formattedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        int priceDiff = 0;
                        for(QueryDocumentSnapshot document: task.getResult()) {
                            priceDiff += Integer.parseInt("" + document.get("priceDiff"));
                        }
                        binding.profitToday.setText("Rp." + formatter.format(priceDiff));
                    } else {
                        binding.profitToday.setText("Rp.0");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}