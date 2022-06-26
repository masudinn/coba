package com.buahq.buahq.graphic_sell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.buahq.buahq.R;
import com.buahq.buahq.databinding.ActivityGraphicSellBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class GraphicSellActivity extends AppCompatActivity {

    private ActivityGraphicSellBinding binding;
    private String year = "2022";
    private int jan = 0;
    private int feb = 0;
    private int mar = 0;
    private int apr = 0;
    private int may = 0;
    private int jun = 0;
    private int jul = 0;
    private int aug = 0;
    private int sep = 0;
    private int oct = 0;
    private int nov = 0;
    private int des = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphicSellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        dropdownYear();
    }

    private void dropdownYear() {
        // filter belum bayar atau sudah bayar, atau selesai
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.year.setAdapter(adapter);
        binding.year.setOnItemClickListener((adapterView, view, i, l) -> {
            binding.barGraph.setVisibility(View.GONE);
            jan = 0;
            feb = 0;
            mar = 0;
            apr = 0;
            may = 0;
            jun = 0;
            jul = 0;
            aug = 0;
            sep = 0;
            oct = 0;
            nov = 0;
            des = 0;
            year = binding.year.getText().toString();
            initViewModel();
        });
    }

    private void initViewModel() {
        GraphicSellViewModel viewModel = new ViewModelProvider(this).get(GraphicSellViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(year == null) {
            viewModel.setGraphicSellList();
        } else {
            viewModel.setGraphicSellByYear(year);
        }
        viewModel.getGraphicSell().observe(this, this::showData);
    }

    private void showData(ArrayList<GraphicSellModel> graphicSell) {
        jan = 0;
        feb = 0;
        mar = 0;
        apr = 0;
        may = 0;
        jun = 0;
        jul = 0;
        aug = 0;
        sep = 0;
        oct = 0;
        nov = 0;
        des = 0;

        for(int i=0; i<graphicSell.size(); i++) {
            switch (graphicSell.get(i).getMonth()) {
                case "Jan":
                    jan++;
                    break;
                case "Feb":
                    feb++;
                    break;
                case "Mar":
                    mar++;
                    break;
                case "Apr":
                    apr++;
                    break;
                case "May":
                    may++;
                    break;
                case "Jun":
                    jun++;
                    break;
                case "Jul":
                    jul++;
                    break;
                case "Aug":
                    aug++;
                    break;
                case "Sep":
                    sep++;
                    break;
                case "Oct":
                    oct++;
                    break;
                case "Nov":
                    nov++;
                    break;
                case "Dec":
                    des++;
                    break;
            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.barGraph.setVisibility(View.VISIBLE);
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(jan, 0));
            barEntries.add(new BarEntry(feb, 1));
            barEntries.add(new BarEntry(mar, 2));
            barEntries.add(new BarEntry(apr, 3));
            barEntries.add(new BarEntry(may, 4));
            barEntries.add(new BarEntry(jun, 5));
            barEntries.add(new BarEntry(jul, 6));
            barEntries.add(new BarEntry(aug, 7));
            barEntries.add(new BarEntry(sep, 8));
            barEntries.add(new BarEntry(oct, 9));
            barEntries.add(new BarEntry(nov, 10));
            barEntries.add(new BarEntry(des, 11));
            BarDataSet barDataSet = new BarDataSet(barEntries, "Penjualan Per Bulan");

            ArrayList<String> theDates = new ArrayList<>();
            theDates.add("Jan");
            theDates.add("Feb");
            theDates.add("Mar");
            theDates.add("Apr");
            theDates.add("May");
            theDates.add("Jun");
            theDates.add("Jul");
            theDates.add("Aug");
            theDates.add("Sep");
            theDates.add("Oct");
            theDates.add("Nov");
            theDates.add("Des");

            BarData theData = new BarData(theDates, barDataSet);
            binding.barGraph.setData(theData);
            binding.barGraph.setTouchEnabled(true);
            binding.barGraph.setDragEnabled(true);
            binding.barGraph.setScaleEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }, 3000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}