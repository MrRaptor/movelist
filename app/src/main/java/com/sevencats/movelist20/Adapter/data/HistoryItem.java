package com.sevencats.movelist20.Adapter.data;

import android.database.Cursor;

public class HistoryItem {
    private String from;
    private String to;
    private String mail;
    private double sum;
    private long id;

    public void setFrom(String from){
        this.from = from;
    }
    public String getFrom(){
        return from;
    }

    public void setTo(String to){this.to = to; }
    public String getTo(){ return to; }

    public void setMail(String mail){this.mail = mail; }
    public String getMail(){ return mail; }

    public void setSum(double sum){this.sum = sum; }
    public double getSum(){ return sum; }

    public void setId(long id){
        this.id = id;
    }
    public long getId(){
        return id;
    }

    public static HistoryItem fromCursor(Cursor cursor) {
        HistoryItem listItem = new HistoryItem();
        listItem.setFrom(cursor.getString(cursor.getColumnIndex("fromDate")));
        listItem.setTo(cursor.getString(cursor.getColumnIndex("toDate")));
        listItem.setMail(cursor.getString(cursor.getColumnIndex("mail")));
        listItem.setSum(cursor.getDouble(cursor.getColumnIndex("sum")));
        listItem.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        return listItem;
    }
}
