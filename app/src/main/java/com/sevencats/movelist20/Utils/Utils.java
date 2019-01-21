package com.sevencats.movelist20.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.sevencats.movelist20.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utils {

    public static double movesSum;

    public static String getDateFormat(int day, int month, int year) {
        String StrDay, StrMonth;
        if (String.valueOf(day).length() == 1) {
            StrDay = "0" + day;
        } else StrDay = String.valueOf(day);
        if (String.valueOf(month).length() == 1) {
            StrMonth = "0" + month;
        } else StrMonth = String.valueOf(month);
        return StrDay + "." + StrMonth + "." + String.valueOf(year);
    }

    public static String timeFormat(int hours, int minute) {
        if (String.valueOf(minute).length() == 1) {
            return String.valueOf(hours) + " : " + "0" + String.valueOf(minute);
        } else
            return String.valueOf(hours) + " : " + String.valueOf(minute);
    }

    public static String dateCostFormat(double cost) {
        {
            if (cost == (long) cost)
                return String.format("%d", (long) cost);
            else
                return String.format("%s", cost);
        }
    }

    public static String getCurrentDate(SimpleDateFormat simpleDateFormat) {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return simpleDateFormat.format(c.getTime());
    }

    public static void setStringSharedPref(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setIntSharedPref(String key, int value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getStringSharedPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static int getIntSharedPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    public static String getMoves(String from, String to) {
        ArrayList<String> mailAddress = new ArrayList<>();
        Cursor cursor = MainActivity.db.daoMoves().getMoves();
        movesSum = 0;
        if (cursor.moveToFirst()) {
            int id = cursor.getColumnIndex("_id");
            int firstAddressIndex = cursor.getColumnIndex("inAddress");
            int lastAddressIndex = cursor.getColumnIndex("outAddress");
            int DateIndex = cursor.getColumnIndex("date");
            int PriceIndex = cursor.getColumnIndex("price");
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date d;
                try {
                    d = dateFormat.parse(cursor.getString(DateIndex));
                    Date dateFrom = dateFormat.parse(from);
                    Date dateTo = dateFormat.parse(to);
                    if (d.getTime() >= dateFrom.getTime() && d.getTime() <= dateTo.getTime()) {
                        MainActivity.db.daoMoves().forwarded(cursor.getLong(id));
                        mailAddress.add(cursor.getString(DateIndex) + " " + cursor.getString(firstAddressIndex) + " - " + cursor.getString(lastAddressIndex) + " " + cursor.getDouble(PriceIndex) + "  грн");
                        movesSum = movesSum + cursor.getDouble(PriceIndex);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < mailAddress.size(); i++) {
            strBuilder.append(mailAddress.get(i) + "\n");
        }

        String finalMailText = strBuilder.toString();
        return finalMailText + "\n" + "Загальна сума : " + movesSum + " грн";
    }

    /**
     * check if date entrance in period
     */

    public static List<String> ifDateEntrance(String from, String to) throws ParseException {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date dateFrom = dateFormat.parse(from);
        Date dateTo = dateFormat.parse(to);

        for (String date : MainActivity.db.daoMoves().getDatesList()) {
            Date d = dateFormat.parse(date);
            if(d.getTime() >= dateFrom.getTime() && d.getTime() <= dateTo.getTime()){
                dateList.add(date);
            }
        }
        return dateList;
    }

    public static double sumOfPeriod(String from, String to) throws ParseException {
        double sum = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date dateFrom = dateFormat.parse(from);
        Date dateTo = dateFormat.parse(to);

        for (String date : MainActivity.db.daoMoves().getDatesList()) {
            Date d = dateFormat.parse(date);
            if(d.getTime() >= dateFrom.getTime() && d.getTime() <= dateTo.getTime()){
                sum += MainActivity.db.daoMoves().getDatesSum(date);
            }
        }
        return sum;
    }



   /* private Map<String, Integer> getDataFromCursor(@NonNull Cursor cursor){

        Map<String, Integer> values = new HashMap<>();
        if (cursor.moveToFirst()) {
            int date = cursor.getColumnIndex("date");
            int price = cursor.getColumnIndex("price");

            do {
                if(values.get(cursor.getString(date))!= null){
                    values.put(cursor.getString(date),values.get(cursor.getString(date) + (int)cursor.getDouble(price)));
                }
                else {
                    values.put(cursor.getString(date),(int) cursor.getDouble(price));
                }
            }
            while (cursor.moveToNext());
        }
        else {
            cursor.close();
        }
        return values;
    }*/
}
