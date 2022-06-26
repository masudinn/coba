package com.buahq.buahq.produk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.buahq.buahq.R;
import com.buahq.buahq.databinding.ActivityProductUpdateBinding;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProductUpdateActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "product";
    private ActivityProductUpdateBinding binding;
    private ProductModel model;
    private String dp;
    private int totalSelling = 0;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_PRODUCT);
        Glide.with(this)
                .load(model.getDp())
                .into(binding.productDp);

        binding.nameEt.setText(model.getName());
        binding.priceBaseEt.setText(String.valueOf(model.getPriceBase()));
        binding.priceFinalEt.setText(String.valueOf(model.getPriceFinal()));

        // ambil total produk
        getTotalSelling();
        // hapus produk
        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProductUpdateActivity.this)
                        .setTitle("Konfirmasi Hapus Produk")
                        .setMessage("Apakah anda yakin ingin menghapus produk ini ?")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("YA", (dialogInterface, i) -> {
                            // hapus produk
                            deleteProduct();
                        })
                        .setNegativeButton("TIDAK", (dialog, i) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        // KLIK TAMBAH GAMBAR
        binding.imageHint.setOnClickListener(view -> ImagePicker.with(ProductUpdateActivity.this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY));

        // update produk
        binding.updateBtn.setOnClickListener(view -> formValidation());

    }

    private void getTotalSelling() {
        FirebaseFirestore
                .getInstance()
                .collection("produk_terjual")
                .whereEqualTo("productId", model.getProductId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                totalSelling += Integer.parseInt("" + document.get("qty"));
                            }
                            binding.totalSellingEt.setText("" + totalSelling);
                        } else  {
                            binding.totalSellingEt.setText(0);
                        }
                    }
                });
    }


    private void formValidation() {
        String name = binding.nameEt.getText().toString().trim();
        String modal = binding.priceBaseEt.getText().toString().trim();
        String price = binding.priceFinalEt.getText().toString();


        if(name.isEmpty()) {
            Toast.makeText(ProductUpdateActivity.this, "Nama Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(modal.isEmpty()) {
            Toast.makeText(ProductUpdateActivity.this, "Modal Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price.isEmpty()) {
            Toast.makeText(ProductUpdateActivity.this, "Harga Jual Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("nameTemp", name.toLowerCase());
        product.put("priceBase", Integer.parseInt(modal));
        product.put("priceFinal", Integer.parseInt(price));
        product.put("priceDiff", Integer.parseInt(price) - Integer.parseInt(modal));
        if(dp != null) {
            product.put("dp", dp);
        }


        FirebaseFirestore
                .getInstance()
                .collection("product")
                .document(model.getProductId())
                .update(product)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        showSuccessDialog("Berhasil Memperbarui Produk", "Anda berhasil memperbarui produk ini");
                    }
                    else {
                        binding.progressBar.setVisibility(View.GONE);
                        showFailureDialog("Gagal Memperbarui Produk", "Terdapat kesalahan ketika memperbarui produk, silahkan periksa koneksi internet anda, dan coba lagi nanti");
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data.getData());
            }
        }
    }

    private void uploadArticleDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "product/img_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    mProgressDialog.dismiss();
                                    dp = uri.toString();
                                    binding.imageHint.setVisibility(View.GONE);
                                    Glide
                                            .with(this)
                                            .load(dp)
                                            .into(binding.productDp);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ProductUpdateActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(ProductUpdateActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    private void deleteProduct() {
        FirebaseFirestore
                .getInstance()
                .collection("product")
                .document(model.getProductId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog("Berhasil Menghapus Produk", "Anda berhasil menghapus produk ini");
                        } else {
                            showFailureDialog("Gagal Menghapus Produk", "Terdapat kesalahan ketika menghapus produk, silahkan periksa koneksi internet anda, dan coba lagi nanti");
                        }
                    }
                });
    }


    private void showFailureDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}