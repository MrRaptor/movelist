package com.sevencats.movelist20;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Pair;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sevencats.movelist20.Adapter.MainRecyclerAdapter;
import com.sevencats.movelist20.Listener.MainCardListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity implements MainCardListener {

    private RecyclerView recyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        setupPieChart();

        recyclerList = findViewById(R.id.recycler_view);
        recyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerList);

        ImageView back_btn = findViewById(R.id.back_arrow);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupPieChart(){
        PieChart pieChart = findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.4f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.animateY(1200, Easing.EasingOption.EaseInOutCubic);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                setupAdapter(Integer.valueOf(((PieEntry) e).getLabel()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        ArrayList<PieEntry> yValues = new ArrayList<>();
        for (Map.Entry<Integer, Integer> data : getDataSet().entrySet()) {
            yValues.add(new PieEntry(data.getValue(), data.getKey().toString()));
        }

        PieDataSet dataSet = new PieDataSet(yValues, null);
        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);
        dataSet.setValueTextSize(0);
        dataSet.setColors(getChartColors());

        PieData data = new PieData(dataSet);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.setData(data);
    }

    private List<Integer> getChartColors(){
        List<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.colorPie1));
        colors.add(getResources().getColor(R.color.colorPie2));
        colors.add(getResources().getColor(R.color.colorPie3));
        colors.add(getResources().getColor(R.color.colorPie4));
        colors.add(getResources().getColor(R.color.colorPie5));
        colors.add(getResources().getColor(R.color.colorPie6));
        colors.add(getResources().getColor(R.color.colorPie7));

        return colors;
    }

    /**  return list of unique date cost  */
    private HashSet<Integer> getUniqueDateCost() {
        HashSet<Integer> uniqueCost = new HashSet<>();

        for (String s : MainActivity.db.daoMoves().getDatesList()) {
            uniqueCost.add((int) MainActivity.db.daoMoves().getDatesSum(s));
        }
        return uniqueCost;
    }

    /** return data for PieChart  */
    private Map<Integer, Integer> getDataSet() {
        Map<Integer, Integer> data = new HashMap<>();
        for (Integer cost : getUniqueDateCost()) {
            int count = 0;
            for (String date : MainActivity.db.daoMoves().getDatesList()) {
                if (MainActivity.db.daoMoves().getDatesSum(date) == cost) {
                    count++;
                }
            }
            data.put(cost, count);
        }
        return data;
    }

    /** return date list for selected cost  */
    private List<String> getDatesList(Integer dateCost) {
        List<String> list = new ArrayList<>();
        for (String date : MainActivity.db.daoMoves().getDatesList()) {
            if (MainActivity.db.daoMoves().getDatesSum(date) == dateCost) {
                list.add(date);
            }
        }
        return list;
    }

    private void setupAdapter(Integer dateCost) {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(AnalyticsActivity.this, R.anim.layout_analytics_anim);
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(this, this, getDatesList(dateCost));
        recyclerList.setAdapter(adapter);
        recyclerList.setLayoutAnimation(controller);
        recyclerList.getAdapter().notifyDataSetChanged();
        recyclerList.scheduleLayoutAnimation();
    }

    @Override
    public void onClickDateCard(String date, int position) {
        View currentView = recyclerList.getLayoutManager().findViewByPosition(position);
        TextView moveDate = currentView.findViewById(R.id.move_date);
        TextView dateSum = currentView.findViewById(R.id.date_sum);
        Intent intent = new Intent(AnalyticsActivity.this, ListActivity.class);
        intent.putExtra("date", date);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(moveDate, "move_date");
        pairs[1] = new Pair<View, String>(dateSum, "date_sum");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AnalyticsActivity.this, pairs);
        startActivity(intent, options.toBundle());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
