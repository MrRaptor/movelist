package com.sevencats.movelist20.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;
import java.util.Map;

@Dao
public interface DaoMoves {

    @Insert
    void addMove (TableMoves table);

    @Query("select * from Moves order by date asc")
    Cursor getMoves();

    @Query("select count(_id) from Moves where isForwarded = 0")
    int getMovesCount();

    @Query("select distinct date from Moves where isForwarded = 0 order by date asc")
    List<String> getDatesIsForwarded();

    @Query("select distinct date from Moves order by date asc")
    List<String> getDatesList();

    @Query("select sum(price) from Moves where date = :date ")
    double getDatesSum(String date);

    @Query("select price from Moves where _id = :id ")
    double getPriceFromID(long id);

    @Query("select * from Moves where date = :date ")
    Cursor getDateMoves(String date);

    @Query("delete from Moves where _id = :id")
    void delRec(long id);

    @Query("update Moves set inAddress = :inAddress , outAddress = :outAddress, date = :date where _id = :id")
    void updateRec(String inAddress, String outAddress, String date, long id );

    @Query("update Moves set isForwarded = 1 where _id = :id")
    void forwarded(long id);

}
