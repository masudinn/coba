package com.buahq.buahq.produk;

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
import com.buahq.buahq.databinding.ActivityProductAddBinding;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProductAddActivity extends AppCompatActivity {

    private ActivityProductAddBinding binding;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // KLIK TAMBAH GAMBAR
        binding.imageHint.setOnClickListener(view -> ImagePicker.with(ProductAddActivity.this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY));

        // upload produk
        binding.uploadProductBtn.setOnClickListener(view -> formValidation());

    }

    private void formValidation() {
        String name = binding.nameEt.getText().toString().trim();
        String modal = binding.priceBaseEt.getText().toString().trim();
        String price = binding.priceFinalEt.getText().toString();


        if(name.isEmpty()) {
            Toast.makeText(ProductAddActivity.this, "Nama Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(modal.isEmpty()) {
            Toast.makeText(ProductAddActivity.this, "Modal Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price.isEmpty()) {
            Toast.makeText(ProductAddActivity.this, "Harga Jual Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(dp == null) {
            Toast.makeText(ProductAddActivity.this, "Gambar Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = String.valueOf(System.currentTimeMillis());

        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("nameTemp", name.toLowerCase());
        product.put("priceBase", Integer.parseInt(modal));
        product.put("priceFinal", Integer.parseInt(price));
        product.put("priceDiff", Integer.parseInt(price) - Integer.parseInt(modal));
        product.put("totalSelling", 0);
        product.put("dp", dp);
        product.put("productId", uid);


        FirebaseFirestore
                .getInstance()
                .collection("product")
                .document(uid)
                .set(product)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        showSuccessDialog();
                    }
                    else {
                        binding.progressBar.setVisibility(View.GONE);
                        showFailureDialog();
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Mengunggah Produk")
                .setMessage("Terdapat kesalahan ketika mengunggah produk, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengunggah Produk")
                .setMessage("Produk akan segera terbit, anda dapat mengedit atau menghapus produk jika terdapat kesalahan")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
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
                                    Toast.makeText(ProductAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(ProductAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}