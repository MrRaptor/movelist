package com.sevencats.movelist20.Database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {TableMoves.class, TableSettings.class, TableSendHistory.class},version = 1)
public abstract class MoveDB extends RoomDatabase {
    public abstract DaoMoves daoMoves();
    public abstract DaoSettings daoSettings();
    public abstract DaoSendHistory daoSendHistory();
}
