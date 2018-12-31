package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "Moves")
public class TableMoves {

    @PrimaryKey(autoGenerate = true)
    public long _id;
    public String inAddress;
    public String outAddress;
    public String date;
    public double price;
    public int isForwarded;
}
