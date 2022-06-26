package com.buahq.buahq.laporan;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buahq.buahq.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final ArrayList<ReportModel> listReport = new ArrayList<>();
    public void setData(ArrayList<ReportModel> items) {
        listReport.clear();
        listReport.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listReport.get(position));
    }

    @Override
    public int getItemCount() {
        return listReport.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, date, total, profit;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.produkTerlaris);
            date = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.total);
            profit = itemView.findViewById(R.id.keuntungan);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ReportModel reportModel) {
            NumberFormat formatter = new DecimalFormat("#,###");

            name.setText("Produk Terlaris: " + reportModel.getTerlaris());
            date.setText("Tanggal: " + reportModel.getDate());
            total.setText("Total: " + reportModel.getTotal());
            profit.setText("Keuntungan Rp." + formatter.format(reportModel.getPriceDiff()));
        }
    }
}
