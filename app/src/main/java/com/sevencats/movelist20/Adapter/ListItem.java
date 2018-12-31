package com.sevencats.movelist20.Adapter;

import android.database.Cursor;

public class ListItem {
    private String inAddress;
    private String outAddress;
    private String date;
    private long id;

    public void setInAddress(String inAddress){
        this.inAddress = inAddress;
    }
    public String getInAddress(){
        return inAddress;
    }

    public void setOutAddress(String outAddress){this.outAddress = outAddress; }
    public String getOutAddress(){ return outAddress; }

    public void setDate(String date){this.date = date; }
    public String getDate(){ return date; }

    public void setId(long id){
        this.id = id;
    }
    public long getId(){
        return id;
    }

    public static ListItem fromCursor(Cursor cursor) {
        ListItem listItem = new ListItem();
        listItem.setInAddress(cursor.getString(cursor.getColumnIndex("inAddress")));
        listItem.setOutAddress(cursor.getString(cursor.getColumnIndex("outAddress")));
        listItem.setDate(cursor.getString(cursor.getColumnIndex("date")));
        listItem.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        return listItem;
    }
}
