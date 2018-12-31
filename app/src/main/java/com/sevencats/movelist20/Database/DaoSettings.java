package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

@Dao
public interface DaoSettings {
    @Query("select  price from Settings")
    double getPrice();

    @Query("select mail from Settings")
    String getMail();

    @Query("select * from settings")
    Cursor getSettings();

    @Insert
    void addSettings(TableSettings table);

    @Query("update Settings set mail = :mail , price = :price where _id = 1")
    void updSettings(String mail, Double price);
}
