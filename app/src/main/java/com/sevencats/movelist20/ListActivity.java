package com.sevencats.movelist20;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sevencats.movelist20.Adapter.ListRecyclerViewAdapter;
import com.sevencats.movelist20.Database.TableMoves;
import com.sevencats.movelist20.Listener.ListCardListener;
import com.sevencats.movelist20.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ListActivity extends AppCompatActivity implements ListCardListener {

    private ListRecyclerViewAdapter adapter;
    private TextView dateSum;
    private String cardDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dateSum = findViewById(R.id.date_sum);
        TextView moveDate = findViewById(R.id.move_date);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cardDate = bundle.getString("date");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ListRecyclerViewAdapter(this, MainActivity.db.daoMoves().getDateMoves(cardDate), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        moveDate.setText(cardDate);
        dateSum.setText(Double.toString(MainActivity.db.daoMoves().getDatesSum(cardDate)));
    }



    /** ListCardListener */
    @Override
    public void onDeleteCard() {
       calculateDaySum();
    }

    /** flag = 1  copy moves to another date */
    @Override
    public void onCopyCard(long id, String inAddress, String outAddress, String date, int flag) {
        showCardDialog(id,inAddress,outAddress,date,flag);
    }

    /** flag = 0 update moves */
    @Override
    public void onClickCard(final long id , String inAddress, String outAddress, String date, int flag) {
        showCardDialog(id,inAddress,outAddress,date, flag);
    }

    private void showCardDialog(final long id , String inAddress, String outAddress, String date, final int flag){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.dialog_edit_address, null);

        final TextView dialogInAddress = dialogView.findViewById(R.id.inAddress);
        final TextView dialogOutAddress = dialogView.findViewById(R.id.outAddress);
        final TextView dialogDate = dialogView.findViewById(R.id.date);
        Button btnOk = dialogView.findViewById(R.id.ok_btn);
        Button btnCancel = dialogView.findViewById(R.id.cancel_btn);


        Calendar myCurrentDate = Calendar.getInstance();
        final int day = myCurrentDate.get(Calendar.DAY_OF_MONTH);
        final int month = myCurrentDate.get(Calendar.MONTH);
        final int year = myCurrentDate.get(Calendar.YEAR);

        dialogDate.setText(date);
        dialogInAddress.setText(inAddress);
        dialogOutAddress.setText(outAddress);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.AddDialogStyle;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ListActivity.this, R.style.datepicker,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        dialogDate.setText(Utils.getDateFormat(dayOfMonth, month, year));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0){
                   updateCard(id,dialogInAddress.getText().toString(),dialogOutAddress.getText().toString(),dialogDate.getText().toString());
                }
                else if(flag == 1){
                    copyCard(id,dialogInAddress.getText().toString(),dialogOutAddress.getText().toString(), dialogDate.getText().toString());
                }
                dialog.dismiss();
                calculateDaySum();
                adapter.updateMoves(MainActivity.db.daoMoves().getDateMoves(cardDate));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void copyCard (long id, String inAddress, String outAddress, String date){
        TableMoves tableMoves = new TableMoves();
        tableMoves.inAddress = inAddress;
        tableMoves.outAddress = outAddress;
        tableMoves.date = date;
        tableMoves.price = MainActivity.db.daoMoves().getPriceFromID(id);
        tableMoves.isForwarded = 0;
        MainActivity.db.daoMoves().addMove(tableMoves);
    }

    private void updateCard(long id, String inAddress, String outAddress, String date){
        MainActivity.db.daoMoves().updateRec(inAddress,outAddress,date,id);
    }

    private void calculateDaySum(){
        Double sumOfDay = MainActivity.db.daoMoves().getDatesSum(cardDate);
        dateSum.setText(Double.toString(sumOfDay));
        if( sumOfDay == 0) {
            super.onBackPressed();
        }
    }
}
