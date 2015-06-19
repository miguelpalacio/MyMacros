package com.miguelpalacio.mymacros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
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
import com.miguelpalacio.mymacros.database.DatabaseAdapter;
import com.miguelpalacio.mymacros.database.datatypes.MacrosConsumed;
import com.miguelpalacio.mymacros.database.datatypes.WeeklyConsumption;
import com.miguelpalacio.mymacros.database.datatypes.WeightLogs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

        setMacrosBarChartData();


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

        setCaloriesBarChartData();


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

        weightLineChart.setMarkerView(mv);

        Legend weightLegend = weightLineChart.getLegend();
        weightLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        weightLegend.setYOffset(12f);
        weightLegend.setTextSize(11f);

        YAxis weightLeftAxis = weightLineChart.getAxisLeft();
        weightLeftAxis.setStartAtZero(false);
        weightLeftAxis.setAxisMinValue(70f);

        weightLineChart.getAxisRight().setEnabled(false);

        setWeightLineChartData();
    }

    public void setMacrosBarChartData() {
        int numberOfDays = 4;
        MacrosConsumed macrosConsumed = databaseAdapter.getMacrosConsumed(numberOfDays);

        List<Double> proteinConsumed;
        List<Double> carbsConsumed;
        List<Double> fatConsumed;
        List<Long> dateLogs;

        proteinConsumed = macrosConsumed.getProteinConsumed();
        carbsConsumed = macrosConsumed.getCarbsConsumed();
        fatConsumed = macrosConsumed.getFatConsumed();
        dateLogs = macrosConsumed.getDateLogs();

        int listLimit = dateLogs.size() - 1;

        // Load the labels for the X axis.
        Calendar c = Calendar.getInstance();

        ArrayList<String> xValues = new ArrayList<>();
        for (int i = listLimit; i >= 0; i--) {
            c.setTimeInMillis(dateLogs.get(i));
            c.add(Calendar.DAY_OF_MONTH, -1);
            xValues.add(c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH));
        }

        // Load values for each macronutrient.
        ArrayList<BarEntry> proteinValues = new ArrayList<>();
        ArrayList<BarEntry> carbsValues = new ArrayList<>();
        ArrayList<BarEntry> fatValues = new ArrayList<>();

        for (int i = listLimit; i >= 0; i--) {
            float value = (float) proteinConsumed.get(i).doubleValue();
            proteinValues.add(new BarEntry(value, listLimit - i));
        }

        for (int i = listLimit; i >= 0; i--) {
            float value = (float) carbsConsumed.get(i).doubleValue();
            carbsValues.add(new BarEntry(value, listLimit - i));
        }

        for (int i = listLimit; i >= 0; i--) {
            float value = (float) fatConsumed.get(i).doubleValue();
            fatValues.add(new BarEntry(value, listLimit - i));
        }

        // Create one dataset for each macro.
        BarDataSet proteinSet = new BarDataSet(proteinValues, "Protein (g)");
        proteinSet.setColor(getResources().getColor(R.color.color_protein));
        BarDataSet carbsSet = new BarDataSet(carbsValues, "Carbohydrates (g)");
        carbsSet.setColor(getResources().getColor(R.color.color_carbs));
        BarDataSet fatSet = new BarDataSet(fatValues, "Fat (g)");
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


    public void setCaloriesBarChartData() {
        int numberOfWeeks = 4;
        WeeklyConsumption weeklyConsumption = databaseAdapter.getWeeklyConsumption(numberOfWeeks);

        List<Double> caloriesConsumed = weeklyConsumption.getCaloriesConsumed();
        List<String> weeks = weeklyConsumption.getWeeks();

        int listLimit = weeks.size() - 1;

        // Load the labels for the X axis.
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = listLimit; i >= 0; i--) {
            xValues.add(weeks.get(i));
        }

        ArrayList<BarEntry> calorieValues = new ArrayList<>();

        for (int i = listLimit; i >= 0; i--) {
            float value = (float) caloriesConsumed.get(i).doubleValue();
            calorieValues.add(new BarEntry(value, listLimit - i));
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

    private void setWeightLineChartData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weightUnits = prefs.getString(SettingsFragment.KEY_WEIGHT, "");

        int numberOfDays = 30;
        WeightLogs weightLogs = databaseAdapter.getWeightLogs(numberOfDays);

        List<Double> weights;
        List<Long> dateLogs;

        weights = weightLogs.getWeights();
        dateLogs = weightLogs.getDateLogs();

        int listLimit = dateLogs.size() - 1;

        // Load the labels for the X axis.
        Calendar c = Calendar.getInstance();

        ArrayList<String> xValues = new ArrayList<>();
        for (int i = listLimit; i >= 0; i--) {
            c.setTimeInMillis(dateLogs.get(i));
            c.add(Calendar.DAY_OF_MONTH, -1);
            xValues.add(c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH));
        }

        ArrayList<Entry> weightValues = new ArrayList<>();

        for (int i = listLimit; i >= 0; i--) {
            float value = (float) weights.get(i).doubleValue();
            value = weightUnits.equals("lb") ? (float) (value * 2.2046) : value;
            weightValues.add(new BarEntry(value, listLimit - i));
        }

        LineDataSet weightSet = new LineDataSet(weightValues, "Weight (" + weightUnits + ")");

        weightSet.setColor(getResources().getColor(R.color.color_weight));
        weightSet.setCircleColor(getResources().getColor(R.color.color_weight));
        weightSet.setLineWidth(1f);
        weightSet.setCircleSize(3f);
        weightSet.setDrawCircles(false);
        weightSet.setDrawCircleHole(false);
        weightSet.setValueTextSize(9f);
        weightSet.setDrawValues(false);
/*        weightSet.setFillAlpha(65);
        weightSet.setFillColor(Color.BLACK);*/
        weightSet.setDrawCubic(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(weightSet);

        LineData data = new LineData(xValues, dataSets);

        // Adjust Y Axis to show the weight with a nice "resolution".
        YAxis weightLeftAxis = weightLineChart.getAxisLeft();
        int minWeightIndex;
        int maxWeightIndex;
        if (weights.size() > 0) {
            minWeightIndex = weights.indexOf(Collections.min(weights));
            maxWeightIndex = weights.indexOf(Collections.max(weights));

            weightLeftAxis.setAxisMinValue((float) weights.get(minWeightIndex).doubleValue() - 2);
            weightLeftAxis.setAxisMaxValue((float) weights.get(maxWeightIndex).doubleValue() + 2);
        }

        // Draw the chart.
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
