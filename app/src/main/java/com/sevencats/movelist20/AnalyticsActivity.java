package com.sevencats.movelist20;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyticsActivity extends AppCompatActivity {

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        pieChart = findViewById(R.id.pie_chart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.4f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(AnalyticsActivity.this, ((PieEntry) e).getLabel() ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        pieChart.animateY(1200, Easing.EasingOption.EaseInOutCubic);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        for(Map.Entry<Integer, Integer> data : getDataSet().entrySet()){
            yValues.add(new PieEntry(data.getValue(),data.getKey().toString()));
        }

        PieDataSet dataSet = new PieDataSet(yValues, null);

        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);
        dataSet.setValueTextSize(0);

        List<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.colorPie1));
        colors.add(getResources().getColor(R.color.colorPie2));
        colors.add(getResources().getColor(R.color.colorPie3));
        colors.add(getResources().getColor(R.color.colorPie4));
        colors.add(getResources().getColor(R.color.colorPie5));
        colors.add(getResources().getColor(R.color.colorPie6));
        colors.add(getResources().getColor(R.color.colorPie7));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.setData(data);
    }

    private HashSet<Integer> getUniqueDateCost(){
        HashSet<Integer> uniqueCost = new HashSet<>();

        for(String s : MainActivity.db.daoMoves().getDatesList()){
            uniqueCost.add( (int) MainActivity.db.daoMoves().getDatesSum(s));
        }
    return uniqueCost;
    }

    private Map<Integer,Integer> getDataSet (){
        Map<Integer, Integer> data = new HashMap<>();
        for(Integer setItem : getUniqueDateCost()){
            int count = 0;
            for(String date : MainActivity.db.daoMoves().getDatesList()){
                if(MainActivity.db.daoMoves().getDatesSum(date) == setItem){
                    count++;
                }
            }
            data.put(setItem,count);
        }
    return data;
    }
}
