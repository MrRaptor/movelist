package com.sevencats.movelist20;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.sevencats.movelist20.Database.TableSettings;
import com.sevencats.movelist20.Notification.Notification;
import com.sevencats.movelist20.Utils.Utils;

public class SettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    EditText email, price;
    TextView tvTime;
    ImageView backArrow;
    public final static String SAVED_NOTIFICATION_HOURS = "NotificationHours";
    public final static String SAVED_NOTIFICATION_MINUTES = "NotificationMinutes";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Cursor cursor = MainActivity.db.daoSettings().getSettings();
        TableSettings tableSettings = new TableSettings();
        if (cursor.getCount() == 0) {
            tableSettings.mail = "example@mail.com";
            tableSettings.price = 5.0;
            MainActivity.db.daoSettings().addSettings(tableSettings);
        }

        email = findViewById(R.id.email);
        price = findViewById(R.id.price);
        backArrow = findViewById(R.id.back_arrow);
        email.setText(MainActivity.db.daoSettings().getMail());
        price.setText(Double.toString(MainActivity.db.daoSettings().getPrice()));

        tvTime = findViewById(R.id.tvTime);
        tvTime.setText(String.valueOf(Utils.getIntSharedPref(SettingsActivity.SAVED_NOTIFICATION_HOURS, this)) + " : " + Utils.getIntSharedPref(SettingsActivity.SAVED_NOTIFICATION_MINUTES, this));
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, R.style.TimePicker, SettingsActivity.this, 0, 10, true);
                timePickerDialog.show();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setNotification(int hours, int minutes) {
        Notification notification = new Notification(this);
        notification.setNotification(hours, minutes);
        Utils.setIntSharedPref(SAVED_NOTIFICATION_HOURS,  hours, this);
        Utils.setIntSharedPref(SAVED_NOTIFICATION_MINUTES,  minutes, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.db.daoSettings().updSettings(email.getText().toString(), Double.valueOf(price.getText().toString()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tvTime.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
        setNotification(hourOfDay, minute);
    }
}
