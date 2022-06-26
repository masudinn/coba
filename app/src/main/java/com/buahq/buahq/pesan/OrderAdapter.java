package com.buahq.buahq.pesan;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.buahq.buahq.R;
import com.buahq.buahq.produk.ProductModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final ArrayList<ProductModel> listProduct = new ArrayList<>();

    String transactionId;
    public OrderAdapter(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setData(ArrayList<ProductModel> items) {
        listProduct.clear();
        listProduct.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderAdapter.ViewHolder holder, int position) {
        holder.bind(listProduct.get(position), transactionId);
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView name, price;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.priceFinal);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ProductModel model, String transactionId) {

            NumberFormat formatter = new DecimalFormat("#,###");



            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());
            price.setText("Rp." + formatter.format(Double.parseDouble(String.valueOf(model.getPriceFinal()))));

            cv.setOnClickListener(view -> {

                Dialog dialog;
                Button btnOrder, btnDismiss, iceBtn, hangatBtn;
                TextView name, priceFinalTv;
                EditText totalEt, keteranganEt;
                ProgressBar pb;

                dialog = new Dialog(itemView.getContext());

                dialog.setContentView(R.layout.popup_order);
                dialog.setCanceledOnTouchOutside(false);


                btnOrder = dialog.findViewById(R.id.orderBtn);
                btnDismiss = dialog.findViewById(R.id.dismissBtn);
                name = dialog.findViewById(R.id.productName);
                totalEt = dialog.findViewById(R.id.total);
                keteranganEt = dialog.findViewById(R.id.keteranganTambahan);
                iceBtn = dialog.findViewById(R.id.ice);
                hangatBtn = dialog.findViewById(R.id.hangat);
                priceFinalTv = dialog.findViewById(R.id.priceFinalTv);

                pb = dialog.findViewById(R.id.progress_bar);

                name.setText(model.getName());


                totalEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!editable.toString().isEmpty()) {
                            priceFinalTv.setText("Rp. " + formatter.format(model.getPriceFinal() * Integer.parseInt(editable.toString())));
                        }
                    }
                });

                ArrayList<String> suhu = new ArrayList<>();

                btnOrder.setOnClickListener(view12 -> {
                    String total = totalEt.getText().toString().trim();
                    String keterangan = keteranganEt.getText().toString().trim();

                    if(total.isEmpty()) {
                        Toast.makeText(itemView.getContext(), "Total minuman tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(keterangan.isEmpty()) {
                        Toast.makeText(itemView.getContext(), "Keterangan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (suhu.size() == 0){
                        Toast.makeText(itemView.getContext(), "Temperatur minuman minuman tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // save data to database
                    pb.setVisibility(View.VISIBLE);
                    String cartId = String.valueOf(System.currentTimeMillis());
                    Map<String, Object> cart = new HashMap<>();
                    cart.put("cartId", cartId);
                    cart.put("productId", model.getProductId());
                    cart.put("name", model.getName());
                    cart.put("total", Integer.parseInt(total));
                    cart.put("keterangan", keterangan);
                    cart.put("temperature", suhu.get(0));
                    cart.put("price", model.getPriceFinal() * Integer.parseInt(total));
                    cart.put("dp", model.getDp());
                    cart.put("priceDiff", model.getPriceDiff() * Integer.parseInt(total));
                    if(transactionId == null) {
                        cart.put("transactionId", "");
                    } else {
                        cart.put("transactionId", transactionId);
                    }

                    FirebaseFirestore
                            .getInstance()
                            .collection("cart")
                            .document(cartId)
                            .set(cart)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    pb.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toast.makeText(itemView.getContext(), "Berhasil Menambahkan Minuman Kedalam Checkout", Toast.LENGTH_SHORT).show();
                                } else {
                                    pb.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toast.makeText(itemView.getContext(), "Gagal Menambahkan Minuman Kedalam Checkout", Toast.LENGTH_SHORT).show();
                                }
                            });
                });


                iceBtn.setOnClickListener(view13 -> {
                    suhu.add(0, "Es");
                    iceBtn.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_green_dark));
                    hangatBtn.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.darker_gray));
                });

                hangatBtn.setOnClickListener(view14 -> {
                    suhu.add(0, "Hangat");
                    hangatBtn.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.holo_green_dark));
                    iceBtn.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), android.R.color.darker_gray));
                });

                btnDismiss.setOnClickListener(view1 -> dialog.dismiss());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            });

        }
    }
}
