package com.sevencats.movelist20;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sevencats.movelist20.Adapter.MainRecyclerAdapter;
import com.sevencats.movelist20.Fragment.BottomAnalyticsFragment;
import com.sevencats.movelist20.Listener.AnalyticsFragmentListener;
import com.sevencats.movelist20.Listener.MainCardListener;
import com.sevencats.movelist20.Utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity implements MainCardListener, AnalyticsFragmentListener {

    private RecyclerView recyclerList;
    private String dateFrom = null, dateTo = null;
    private TextView analytics_legend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        try {
            setupPieChart(dateFrom,dateTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        recyclerList = findViewById(R.id.recycler_view);
        recyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerList);

        analytics_legend = findViewById(R.id.analytics_legend);
        ImageView back_btn = findViewById(R.id.back_arrow);
        FloatingActionButton pieFilterBtn = findViewById(R.id.btn_pie_filter);
        pieFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAnalyticsFragment fragment = new BottomAnalyticsFragment();
                fragment.setListener(AnalyticsActivity.this);
                fragment.show(getSupportFragmentManager(),fragment.getTag());
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupPieChart(String dateFrom, String dateTo) throws ParseException {
        PieChart pieChart = findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDragDecelerationFrictionCoef(0.4f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.animateY(1200, Easing.EasingOption.EaseInOutCubic);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                try {
                    setupAdapter(Integer.valueOf(((PieEntry) e).getLabel()));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        PieData data = getPieData(dateFrom, dateTo);
        data.notifyDataChanged();
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private List<Integer> getPieColors() {
        List<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.colorPie1));
        colors.add(getResources().getColor(R.color.colorPie2));
        colors.add(getResources().getColor(R.color.colorPie3));
        colors.add(getResources().getColor(R.color.colorPie4));
        colors.add(getResources().getColor(R.color.colorPie5));
        colors.add(getResources().getColor(R.color.colorPie6));

        return colors;
    }

    private PieData getPieData(String from, String to) throws ParseException {
        ArrayList<PieEntry> entries = new ArrayList<>();
        Map<Integer, Integer> data = getData(from,to);

        for (Map.Entry<Integer, Integer> map : data.entrySet()) {
            entries.add(new PieEntry(map.getValue(), map.getKey().toString()));
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);
        dataSet.setValueTextSize(0);
        dataSet.setColors(getPieColors());

        return new PieData(dataSet);
    }

    /**
     * return list of unique date cost
     */
    private HashSet<Integer> getUniqueDateCost(String from, String to) throws ParseException {
        HashSet<Integer> uniqueCost = new HashSet<>();

        for (String date : Utils.ifDateEntrance(from,to)) {
            uniqueCost.add((int) MainActivity.db.daoMoves().getDatesSum(date));
        }
        return uniqueCost;
    }

    private HashSet<Integer> getUniqueDateCost()  {
        HashSet<Integer> uniqueCost = new HashSet<>();

        for (String date : MainActivity.db.daoMoves().getDatesList()) {
            uniqueCost.add((int) MainActivity.db.daoMoves().getDatesSum(date));
        }
        return uniqueCost;
    }

    /**
     * return data for PieChart
     */
    private Map<Integer, Integer> getData(String from , String to) throws ParseException {
        final Map<Integer, Integer> data = new HashMap<>();

        if(from != null && to != null){
            for (Integer cost : getUniqueDateCost(from,to)) {
                int count = 0;
                for (String date : Utils.ifDateEntrance(from, to)) {
                    if (MainActivity.db.daoMoves().getDatesSum(date) == cost) {
                        count++;
                    }
                }
                data.put(cost, count);
            }
            return data;

        } else{
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
    }

    /**
     * return date list for selected cost
     */
    private List<String> getDatesList(Integer dateCost) throws ParseException {
        List<String> list = new ArrayList<>();
        if(dateFrom != null && dateTo != null) {
            for (String date : Utils.ifDateEntrance(dateFrom,dateTo)) {
                if (MainActivity.db.daoMoves().getDatesSum(date) == dateCost) {
                    list.add(date);
                }
            }
            return list;
        } else {
            for (String date : MainActivity.db.daoMoves().getDatesList()) {
                if (MainActivity.db.daoMoves().getDatesSum(date) == dateCost) {
                    list.add(date);
                }
            }
            return list;
        }
    }

    private void setupAdapter(Integer dateCost) throws ParseException {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(AnalyticsActivity.this, R.anim.layout_analytics_anim);
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(this, this, getDatesList(dateCost));
        recyclerList.setAdapter(adapter);
        recyclerList.setLayoutAnimation(controller);
        recyclerList.getAdapter().notifyDataSetChanged();
        recyclerList.scheduleLayoutAnimation();
    }

    private String getSumOfPeriod(String from, String to){
        try {
            return Utils.dateCostFormat(Utils.sumOfPeriod(from, to));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateFilterSelected(String from, String to) {
        dateFrom = from;
        dateTo = to;

        try {
            setupPieChart(from,to);

            recyclerList.setAdapter(null);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        analytics_legend.setText(from + " - " + to + " : " + getSumOfPeriod(from, to) + " грн.");
    }
}
