package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Settings")
public class TableSettings {
    @PrimaryKey(autoGenerate = true)
    public long _id;
    public String mail;
    public Double price;
}
