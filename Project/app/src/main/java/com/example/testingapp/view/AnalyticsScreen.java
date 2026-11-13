package com.example.testingapp.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.testingapp.R;
import com.example.testingapp.BR;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.viewmodel.AnalyticsViewModel;
import com.example.testingapp.databinding.ActivityAnalyticsScreenBinding;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// chart imports
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.components.XAxis;

// list/map imports
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AnalyticsScreen extends ThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up data binding
        ActivityAnalyticsScreenBinding binding = ActivityAnalyticsScreenBinding.inflate(
                getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // handles window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, 0);
                return insets;
            });

        //Create analyticsViewModel and bind to layout
        AnalyticsViewModel analyticsViewModel = new ViewModelProvider(this).get(
                AnalyticsViewModel.class);

        binding.setVariable(BR.viewModel, analyticsViewModel);
        binding.setLifecycleOwner(this);

        // Get isStaff
        Boolean isStaff = getIntent().getBooleanExtra("isStaff", false);

        // Set up bottom navigation
        setupBottomNavigation(); // Refactored to remove unused parameter

        //setting up the charts
        setupBarChart(analyticsViewModel);
        setupPieChart(analyticsViewModel);
        setupLineChart(analyticsViewModel);
    }

    private void setupBarChart(AnalyticsViewModel analyticsViewModel) {
        BarChart barChart = findViewById(R.id.barChart);
        // Observe issues from AnalyticsViewModel for Bar Chart
        analyticsViewModel.getStatusCounts().observe(this, counts -> {
            // error checking
            if (counts == null || counts.isEmpty()) {
                return;
            }
            List<BarEntry> barEntries = new ArrayList<>();
            List<String> xLabels = new ArrayList<>();

            int idx = 0;
            // setting up bar chart, looping through hashmap & adding values to barEntry
            // (required to add data to bar chart)
            for (HashMap.Entry<String, Integer> entry : counts.entrySet()) {
                barEntries.add(new BarEntry(idx, entry.getValue().floatValue()));
                xLabels.add(entry.getKey());
                idx++;
            }

            BarDataSet barSet = new BarDataSet(barEntries, "Issue Statuses");
            barSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData dataBarChart = new BarData(barSet);
            dataBarChart.setBarWidth(0.5f);
            barChart.setData(dataBarChart);

            // configuring graph
            XAxis xAxis = barChart.getXAxis();
            configureXAxis(xAxis, xLabels);

            barChart.setTouchEnabled(true);
            barChart.setDragEnabled(false);
            barChart.setScaleEnabled(false);
            barChart.setPinchZoom(false);
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setNestedScrollingEnabled(false);
            barChart.setExtraOffsets(10, 10, 10, 20);

            barChart.getDescription().setEnabled(false);
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        });
    }

    private void setupPieChart(AnalyticsViewModel analyticsViewModel) {
        PieChart pieChart = findViewById(R.id.pieChart);
        // Observe issues from AnalyticsViewModel for Pie Chart
        analyticsViewModel.getCategoryCounts().observe(this, counts -> {
            // error checking
            if (counts == null || counts.isEmpty()) {
                return;
            }
            List<PieEntry> pieEntries = new ArrayList<>();
            List<String> xLabels = new ArrayList<>();

            // setting up pie chart, looping through hashmap & adding values to pieEntry
            for (HashMap.Entry<String, Integer> entry : counts.entrySet()) {
                pieEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
                xLabels.add(entry.getKey());
            }

            PieDataSet pieSet = new PieDataSet(pieEntries, "Categories by Issue");
            pieSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData dataPieChart = new PieData(pieSet);
            pieChart.setData(dataPieChart);

            // configuring graph
            pieChart.setTouchEnabled(true);
            pieChart.setRotationEnabled(false);
            pieChart.setHighlightPerTapEnabled(true);
            pieChart.setNestedScrollingEnabled(false);
            pieChart.setExtraOffsets(10, 10, 10, 20);

            pieChart.getDescription().setEnabled(false);
            pieChart.notifyDataSetChanged();
            pieChart.invalidate();
        });
    }

    private void setupLineChart(AnalyticsViewModel analyticsViewModel) {
        LineChart lineChart = findViewById(R.id.lineChart);
        analyticsViewModel.getTimeCounts().observe(this, counts -> {
            // error checking
            if (counts == null || counts.isEmpty()) {
                return;
            }
            List<Entry> lineEntries = new ArrayList<>();
            List<String> xLabels = new ArrayList<>();

            // sorting data in chronological order
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US);

            List<String> sortedDates = getSortedDate(counts, formatter);

            int idx = 0;
            // setting up line chart, looping through hashmap & adding values to entry
            for (String date: sortedDates) {
                lineEntries.add(new Entry(idx, counts.get(date).floatValue()));
                xLabels.add(date);
                idx++;
            }

            LineDataSet lineSet = new LineDataSet(lineEntries, "Issues Over Time");
            // line set up
            lineSet.setColors(ColorTemplate.MATERIAL_COLORS[0]);
            lineSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[0]);
            lineSet.setLineWidth(3f);
            lineSet.setCircleRadius(4f);
            lineSet.setDrawValues(true);
            lineSet.setValueTextSize(11f);
            lineSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            // setting the data
            LineData dataLineChart = new LineData(lineSet);
            lineChart.setData(dataLineChart);

            // configuring graph
            XAxis xAxis = lineChart.getXAxis();
            configureXAxis(xAxis, sortedDates);

            xAxis.setLabelRotationAngle(-45);
            // limits labels show
            xAxis.setLabelCount(Math.min(xLabels.size(), 5), true);
            xAxis.setAvoidFirstLastClipping(true);

            // scroll fixes
            lineChart.setTouchEnabled(true);
            lineChart.setDragEnabled(false);
            lineChart.setScaleEnabled(false);
            lineChart.setNestedScrollingEnabled(false);

            lineChart.getDescription().setEnabled(false);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.setExtraOffsets(10, 10, 10, 20);

            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        });
    }

    private List<String> getSortedDate(
            HashMap<String, Integer> counts, DateTimeFormatter formatter) {
        List<String> sortedDates = new ArrayList<>(counts.keySet());

        // Filter out non-date keys to prevent parsing errors
        sortedDates.remove("No Timestamp");

        if (sortedDates.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.sort(sortedDates, (d1, d2) -> {
            LocalDate date1 = LocalDate.parse(d1, formatter);
            LocalDate date2 = LocalDate.parse(d2, formatter);
            return date1.compareTo(date2);
        });
        return sortedDates;
    }

    private void configureXAxis(XAxis xAxis, List<String> xLabels) {
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                // if index exists, then return
                if (idx >= 0 && idx < xLabels.size()) {
                    return xLabels.get(idx);
                }
                return "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }

    /**
     * Sets up the bottom navigation bar with click listeners for all screens.
     * Staff menu item is only visible to users with staff privileges.
     */
    private void setupBottomNavigation() { // Refactored to remove unused parameter
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem staffMenuItem = menu.findItem(R.id.staff);
        staffMenuItem.setVisible(AccountModel.getInstance().getIsStaff());

        bottomNavigationView.setSelectedItemId(R.id.analytics);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.create) {
                Intent intent = new Intent(AnalyticsScreen.this, IssueCreation.class);
                startActivity(intent);
            } else if (id == R.id.profile) {
                Intent intent = new Intent(AnalyticsScreen.this, ProfileScreen.class);
                startActivity(intent);
            } else if (id == R.id.feed) {
                Intent intent = new Intent(AnalyticsScreen.this, IssueFeed.class);
                startActivity(intent);
            } else if (id == R.id.analytics) {
                return true;
            } else if (id == R.id.staff) {
                Intent intent = new Intent(AnalyticsScreen.this, StaffDashboard.class);
                startActivity(intent);
            } else {
                return false;
            }
            return true;
        });
    }
}