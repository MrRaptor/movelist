package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "SendHistory")
public class TableSendHistory {

    @PrimaryKey(autoGenerate = true)
    public long _id;
    public String fromDate;
    public String toDate;
    public String mail;
    public double sum;
}
