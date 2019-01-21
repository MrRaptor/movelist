package com.sevencats.movelist20.Fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.sevencats.movelist20.Listener.AnalyticsFragmentListener;
import com.sevencats.movelist20.MainActivity;
import com.sevencats.movelist20.R;
import com.sevencats.movelist20.Utils.Utils;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomAnalyticsFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView fromDate,toDate, currentDatePickerView;
    AnalyticsFragmentListener listener;
    public BottomAnalyticsFragment(){}

    public void setListener(AnalyticsFragmentListener listener){
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_analytics, container, false);
        fromDate = view.findViewById(R.id.fromDate);
        toDate = view.findViewById(R.id.toDate);
        Calendar myCurrentDate = Calendar.getInstance();
        final int day = myCurrentDate.get(Calendar.DAY_OF_MONTH);
        final int month = myCurrentDate.get(Calendar.MONTH);
        final int year = myCurrentDate.get(Calendar.YEAR);
        fromDate.setText(Utils.getDateFormat(day, month + 1, year));
        toDate.setText(Utils.getDateFormat(day, month + 1, year));

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDatePickerView = fromDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, BottomAnalyticsFragment.this, year, month, day);
                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDatePickerView = toDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, BottomAnalyticsFragment.this, year, month, day);
                datePickerDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        currentDatePickerView.setText(Utils.getDateFormat(dayOfMonth, month, year));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.onDateFilterSelected(fromDate.getText().toString(),toDate.getText().toString());
    }
}
