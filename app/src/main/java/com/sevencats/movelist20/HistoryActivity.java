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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevencats.movelist20.Adapter.HistoryRecyclerViewAdapter;
import com.sevencats.movelist20.Listener.HistoryCardListener;
import com.sevencats.movelist20.Utils.Utils;

import java.text.SimpleDateFormat;

public class HistoryActivity extends AppCompatActivity implements HistoryCardListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recycler_history);
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(this, MainActivity.db.daoSendHistory().getSendHistory(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        ImageView back_btn = findViewById(R.id.back_arrow);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
