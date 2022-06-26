package com.buahq.buahq.pesan;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buahq.buahq.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final ArrayList<CheckoutModel> cartList = new ArrayList<>();
    public void setData (ArrayList<CheckoutModel> items) {
        cartList.clear();
        cartList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dp;
        TextView name, total, temperature, keteranganTambahan, subTotal;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.roundedImageView);
            name = itemView.findViewById(R.id.name);
            total = itemView.findViewById(R.id.total);
            temperature = itemView.findViewById(R.id.temperature);
            keteranganTambahan = itemView.findViewById(R.id.keteranganTambahan);
            subTotal = itemView.findViewById(R.id.subTotal);

        }

        @SuppressLint("SetTextI18n")
        public void bind(CheckoutModel model) {

            NumberFormat formatter = new DecimalFormat("#,###");


            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());
            total.setText("Total pemesanan: "  + model.getTotal());
            temperature.setText(model.getTemperature());
            keteranganTambahan.setText("Keterangan: " + model.getKeterangan());
            subTotal.setText("Rp." + formatter.format(model.getPrice()));
        }
    }
}
