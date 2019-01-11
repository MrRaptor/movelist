package com.sevencats.movelist20;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.motion.MotionLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.widget.TextView;

import com.sevencats.movelist20.Adapter.HistoryRecyclerViewAdapter;
import com.sevencats.movelist20.Listener.HistoryCardListener;
import com.sevencats.movelist20.Utils.Utils;

import java.text.SimpleDateFormat;

public class HistoryActivity extends AppCompatActivity implements HistoryCardListener {

    private RecyclerView recyclerView;
    private HistoryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recycler_history);
        adapter = new HistoryRecyclerViewAdapter(this, MainActivity.db.daoSendHistory().getSendHistory(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        TextView toDay = findViewById(R.id.toDay);
        MotionLayout analyticsLayout = findViewById(R.id.analytics_layout);
        String sumToDay = String.valueOf(MainActivity.db.daoMoves().getDatesSum(Utils.getCurrentDate(simpleDateFormat)));
        toDay.setText("Проїзди за сьогодні: " + sumToDay + " грн");
        analyticsLayout.transitionToEnd();
    }

    @Override
    public void resendMailOnClick(String from, String to, String mail) {
        String[] receiver = mail.trim().split(", ");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, receiver);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Проїзди ");
        intent.putExtra(Intent.EXTRA_TEXT, Utils.getMoves(from,to));
        intent.setType("massage/rfc822");
        Intent chooser = Intent.createChooser(intent, "Send mail");
        startActivity(chooser);
    }
}
