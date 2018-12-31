package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

@Dao
public interface DaoSendHistory {

    @Insert
    void addRecord(TableSendHistory table);

    @Query("select * from SendHistory")
     Cursor getSendHistory();

    @Query("delete from SendHistory where _id = :id")
    void delRec(long id);

}
