package com.buahq.buahq.produk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.buahq.buahq.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final ArrayList<ProductModel> listProduct = new ArrayList<>();
    public void setData(ArrayList<ProductModel> items) {
        listProduct.clear();
        listProduct.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listProduct.get(position));
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        TextView name;
        ImageView dp;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.name);
            dp = itemView.findViewById(R.id.dp);
        }

        public void bind(ProductModel model) {
            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);


            name.setText(model.getName());

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ProductUpdateActivity.class);
                    intent.putExtra(ProductUpdateActivity.EXTRA_PRODUCT, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
