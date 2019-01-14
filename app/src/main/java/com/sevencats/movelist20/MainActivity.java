package com.sevencats.movelist20;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.sevencats.movelist20.Adapter.MainRecyclerAdapter;
import com.sevencats.movelist20.Database.MoveDB;
import com.sevencats.movelist20.Database.TableMoves;
import com.sevencats.movelist20.Database.TableSendHistory;
import com.sevencats.movelist20.Listener.MainCardListener;
import com.sevencats.movelist20.Utils.GPS;
import com.sevencats.movelist20.Notification.Notification;
import com.sevencats.movelist20.Utils.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MainCardListener, DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerList;
    public ImageView mailBtn, settingsBtn, historyBtn, analyticsBtn;
    private MainRecyclerAdapter adapter;
    public static MoveDB db;
    private GPS gps;
    public final static String SAVED_TEXT_IN_ADDRESS = "saved_in_address";
    public final static String SAVED_TEXT_OUT_ADDRESS = "saved_out_address";
    public TextView currentDatePickerView;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter.updateList(MainActivity.db.daoMoves().getDatesIsForwarded());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkSelfPermission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"Надайте права доступу",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        gps = new GPS(this);

        //DataBase
        db = Room.databaseBuilder(getApplicationContext(), MoveDB.class, "DriveMoves").allowMainThreadQueries().build();

        //Notification resume
        if (db.daoMoves().getMovesCount() > 0) {
            Notification notification = new Notification(this);
            notification.setNotification(Utils.getIntSharedPref(SettingsActivity.SAVED_NOTIFICATION_HOURS, this), Utils.getIntSharedPref(SettingsActivity.SAVED_NOTIFICATION_MINUTES, this));
        }

        FloatingActionButton addAddressBtn = findViewById(R.id.add_address);
        mailBtn = findViewById(R.id.mail_btn);
        settingsBtn = findViewById(R.id.settings_btn);
        historyBtn = findViewById(R.id.history_btn);
        analyticsBtn = findViewById(R.id.analytics_btn);
        setupAdapter();

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRecordDialog();
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSettingBtn();
            }
        });
        mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMailBtn();
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickHistoryBtn();
            }
        });
        analyticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAnalyticsBtn();
            }
        });
    }



    private void setupAdapter() {
        recyclerList = findViewById(R.id.recyclerView);
        adapter = new MainRecyclerAdapter(this, this, db.daoMoves().getDatesIsForwarded());
        recyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerList.setAdapter(adapter);
    }

    private void showAddRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        final View dialogView = inflater.inflate(R.layout.dialog_add_address, null);
        final TextView inAddress = dialogView.findViewById(R.id.inAddress);
        final TextView outAddress = dialogView.findViewById(R.id.outAddress);
        ImageView inGPSAddress = dialogView.findViewById(R.id.inGPSAddress);
        ImageView outGPSAddress = dialogView.findViewById(R.id.outGPSAddress);
        Button btnOk = dialogView.findViewById(R.id.ok_btn);
        Button btnCancel = dialogView.findViewById(R.id.cancel_btn);

        inAddress.setText(loadInAddressText());
        outAddress.setText(loadOutAddressText());

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AddDialogStyle;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                saveAddressText(inAddress.getText().toString(), outAddress.getText().toString());
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecordToDB(inAddress.getText().toString(), outAddress.getText().toString());
                cleanAddressText();
                dialog.setOnDismissListener(null);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        inGPSAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inAddress.setText(gps.getAddress());
            }
        });
        outGPSAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outAddress.setText(gps.getAddress());
            }
        });
    }

    private void addRecordToDB(String inAddress, String outAddress) {
        TableMoves tableMoves = new TableMoves();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        tableMoves.inAddress = inAddress;
        tableMoves.outAddress = outAddress;
        tableMoves.date = Utils.getCurrentDate(simpleDateFormat);
        tableMoves.price = db.daoSettings().getPrice();
        tableMoves.isForwarded = 0;
        db.daoMoves().addMove(tableMoves);
        adapter.updateList(MainActivity.db.daoMoves().getDatesIsForwarded());
    }

    private void addRecordToSendHistory(String fromDate, String toDate, String mail, double sum) {
        TableSendHistory history = new TableSendHistory();
        history.fromDate = fromDate;
        history.toDate = toDate;
        history.mail = mail;
        history.sum = sum;
        db.daoSendHistory().addRecord(history);
        adapter.updateList(MainActivity.db.daoMoves().getDatesIsForwarded());
    }

    private void onClickSettingBtn() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onClickHistoryBtn() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void onClickAnalyticsBtn() {
        Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
        startActivity(intent);
    }

    private void onClickMailBtn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_send_mail, null);
        final TextView fromDate = dialogView.findViewById(R.id.fromDate);
        final TextView toDate = dialogView.findViewById(R.id.toDate);
        final Button btnSend = dialogView.findViewById(R.id.btnSend);
        Calendar myCurrentDate = Calendar.getInstance();
        final int day = myCurrentDate.get(Calendar.DAY_OF_MONTH);
        final int month = myCurrentDate.get(Calendar.MONTH);
        final int year = myCurrentDate.get(Calendar.YEAR);
        fromDate.setText(Utils.getDateFormat(day, month + 1, year));
        toDate.setText(Utils.getDateFormat(day, month + 1, year));

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.SendMailDialogStyle;
        dialog.show();

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDatePickerView = fromDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.datepicker, MainActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDatePickerView = toDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.datepicker, MainActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = db.daoSettings().getMail();
                String[] receiver = mail.trim().split(", ");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, receiver);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Проїзди ");
                intent.putExtra(Intent.EXTRA_TEXT, Utils.getMoves(fromDate.getText().toString(), toDate.getText().toString()));
                intent.setType("massage/rfc822");
                Intent chooser = Intent.createChooser(intent, "Send mail");
                startActivity(chooser);

                //add record to SendHistoryTable
                addRecordToSendHistory(fromDate.getText().toString(), toDate.getText().toString(), mail, Utils.movesSum);
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClickDateCard(String date, int position) {
        View currentView = recyclerList.getLayoutManager().findViewByPosition(position);
        TextView moveDate = currentView.findViewById(R.id.move_date);
        TextView dateSum = currentView.findViewById(R.id.date_sum);
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putExtra("date", date);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(moveDate, "move_date");
        pairs[1] = new Pair<View, String>(dateSum, "date_sum");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
        startActivity(intent, options.toBundle());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Save address when dialog dismiss
    private void saveAddressText(String inAddress, String outAddress) {
        Utils.setStringSharedPref(SAVED_TEXT_IN_ADDRESS, inAddress, this);
        Utils.setStringSharedPref(SAVED_TEXT_OUT_ADDRESS, outAddress, this);
    }

    private void cleanAddressText() {
        Utils.setStringSharedPref(SAVED_TEXT_IN_ADDRESS, null, this);
        Utils.setStringSharedPref(SAVED_TEXT_OUT_ADDRESS, null, this);
    }

    private String loadInAddressText() {
        return Utils.getStringSharedPref(SAVED_TEXT_IN_ADDRESS, this);
    }

    private String loadOutAddressText() {
        return Utils.getStringSharedPref(SAVED_TEXT_OUT_ADDRESS, this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        currentDatePickerView.setText(Utils.getDateFormat(dayOfMonth, month, year));
    }
}


