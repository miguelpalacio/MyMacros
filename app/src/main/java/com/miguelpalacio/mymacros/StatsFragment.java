package com.miguelpalacio.mymacros;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.LargeValueFormatter;

import java.util.ArrayList;

/**
 * Stats Page.
 * Show some user statistics.
 */
public class StatsFragment extends Fragment implements OnChartValueSelectedListener {

    private BarChart macrosBarChart;
    private BarChart caloriesBarChart;
    private LineChart weightLineChart;

    DatabaseAdapter databaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());


        // Create a custom MarkerView (extending MarkerView) and specify its layout.
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        // Macronutrients Previous Days Bar Chart.

        macrosBarChart = (BarChart) getActivity().findViewById(R.id.stats_bar_chart_macros);
        macrosBarChart.setOnChartValueSelectedListener(this);
        macrosBarChart.setDescription("");

        macrosBarChart.setPinchZoom(false);
        macrosBarChart.setDrawBarShadow(false);
        macrosBarChart.setDrawGridBackground(false);

        //macrosBarChart.setMarkerView(mv);

        Legend macrosLegend = macrosBarChart.getLegend();
        macrosLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        macrosLegend.setYOffset(12f);
        macrosLegend.setXEntrySpace(15f);
        macrosLegend.setTextSize(11f);

        YAxis macrosLeftAxis = macrosBarChart.getAxisLeft();
        macrosLeftAxis.setValueFormatter(new LargeValueFormatter());
        macrosLeftAxis.setDrawGridLines(false);
        macrosLeftAxis.setSpaceTop(30f);

        macrosBarChart.getAxisRight().setEnabled(false);

        setBarChartPreviousDaysData();


        // Weekly Intake Bar Chart.

        caloriesBarChart = (BarChart) getActivity().findViewById(R.id.stats_bar_chart_calories);
        caloriesBarChart.setOnChartValueSelectedListener(this);
        caloriesBarChart.setDescription("");

        caloriesBarChart.setPinchZoom(false);
        caloriesBarChart.setDrawBarShadow(false);
        caloriesBarChart.setDrawGridBackground(false);

        //caloriesBarChart.setMarkerView(mv);

        Legend caloriesLegend = caloriesBarChart.getLegend();
        caloriesLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        caloriesLegend.setYOffset(12f);
        caloriesLegend.setXEntrySpace(15f);
        caloriesLegend.setTextSize(11f);

        YAxis caloriesLeftAxis = caloriesBarChart.getAxisLeft();
        caloriesLeftAxis.setValueFormatter(new LargeValueFormatter());
        caloriesLeftAxis.setDrawGridLines(false);
        caloriesLeftAxis.setSpaceTop(30f);

        caloriesBarChart.getAxisRight().setEnabled(false);

        setBarChartWeeklyIntakeData();


        // Weight Progress Line Chart.

        weightLineChart = (LineChart) getActivity().findViewById(R.id.stats_line_chart_weight);
        weightLineChart.setOnChartValueSelectedListener(this);
        weightLineChart.setDrawGridBackground(false);

        weightLineChart.setDescription("");
        weightLineChart.setNoDataTextDescription("Not enough weight data for the chart.");

        weightLineChart.setDragEnabled(true);
        weightLineChart.setScaleEnabled(true);

        weightLineChart.setPinchZoom(false);

        weightLineChart.setHighlightEnabled(false);

        Legend weightLegend = weightLineChart.getLegend();
        weightLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        weightLegend.setYOffset(12f);
        weightLegend.setTextSize(11f);

        YAxis weightLeftAxis = weightLineChart.getAxisLeft();
        weightLeftAxis.setAxisMinValue(0);

        weightLineChart.getAxisRight().setEnabled(false);

        setLineChartWeightProgressData();
    }

    public void setBarChartPreviousDaysData() {
        int l = 5;

        // Load the labels for the X axis.
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            int day = l - i;
            xValues.add("Day " + day);
        }

        // Load values for each macronutrient.
        ArrayList<BarEntry> proteinValues = new ArrayList<>();
        ArrayList<BarEntry> carbsValues = new ArrayList<>();
        ArrayList<BarEntry> fatValues = new ArrayList<>();

        for (int i = 0; i < l; i++) {
            float val = (float) (Math.random() * 100) + 2;
            proteinValues.add(new BarEntry(val, i));
        }

        for (int i = 0; i < l; i++) {
            float val = (float) (Math.random() * 100) + 2;
            carbsValues.add(new BarEntry(val, i));
        }

        for (int i = 0; i < l; i++) {
            float val = (float) (Math.random() * 100) + 2;
            fatValues.add(new BarEntry(val, i));
        }

        // Create one dataset for each macro.
        BarDataSet proteinSet = new BarDataSet(proteinValues, "Protein");
        proteinSet.setColor(getResources().getColor(R.color.color_protein));
        BarDataSet carbsSet = new BarDataSet(carbsValues, "Carbohydrates");
        carbsSet.setColor(getResources().getColor(R.color.color_carbs));
        BarDataSet fatSet = new BarDataSet(fatValues, "Fat");
        fatSet.setColor(getResources().getColor(R.color.color_fat));

        ArrayList<BarDataSet> macrosDataSets = new ArrayList<>();
        macrosDataSets.add(proteinSet);
        macrosDataSets.add(carbsSet);
        macrosDataSets.add(fatSet);

        BarData data = new BarData(xValues, macrosDataSets);

        // Add space between the dataset groups in percent of bar-width.
        data.setGroupSpace(80f);
        data.setValueTextSize(10f);

        macrosBarChart.setData(data);
        macrosBarChart.invalidate();
    }


    public void setBarChartWeeklyIntakeData() {
        int l = 4;

        // Load the labels for the X axis.
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            int day = l - i;
            xValues.add("Week " + day);
        }

        // Load values for each macronutrient.
        ArrayList<BarEntry> calorieValues = new ArrayList<>();


        for (int i = 0; i < l; i++) {
            float val = (float) (Math.random() * 100) + 2;
            calorieValues.add(new BarEntry(val, i));
        }

        // Create one dataset for each macro.
        BarDataSet calorieSet = new BarDataSet(calorieValues, "Calorie Intake");
        calorieSet.setColor(getResources().getColor(R.color.color_fiber_dark));
        calorieSet.setBarSpacePercent(55f);

        ArrayList<BarDataSet> dataSet = new ArrayList<>();
        dataSet.add(calorieSet);

        BarData data = new BarData(xValues, dataSet);

        // Add space between the dataset groups in percent of bar-width.
        data.setValueTextSize(10f);

        caloriesBarChart.setData(data);
        caloriesBarChart.invalidate();
    }

    private void setLineChartWeightProgressData() {

        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            xValues.add((i) + "");
        }

        ArrayList<Entry> yValues = new ArrayList<>();

        for (int i = 0; i < 20; i++) {

            float mult = (100 + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
                                                           // ((mult *
                                                           // 0.1) / 10);
            yValues.add(new Entry(val, i));
        }

        LineDataSet weightSet = new LineDataSet(yValues, "Weight");

        weightSet.setColor(getResources().getColor(R.color.color_weight));
        weightSet.setCircleColor(getResources().getColor(R.color.color_weight));
        weightSet.setLineWidth(1f);
        weightSet.setCircleSize(3f);
        weightSet.setDrawCircles(false);
        weightSet.setDrawCircleHole(false);
        weightSet.setValueTextSize(9f);
/*        weightSet.setFillAlpha(65);
        weightSet.setFillColor(Color.BLACK);*/
        weightSet.setDrawCubic(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(weightSet);

        LineData data = new LineData(xValues, dataSets);

        weightLineChart.setData(data);
        weightLineChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
