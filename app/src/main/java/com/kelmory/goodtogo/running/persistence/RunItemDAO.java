package com.kelmory.goodtogo.running.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RunItemDAO {
    @Query("SELECT * FROM run_history")
    List<RunTableItem> listAll();

    @Query("SELECT * FROM run_history WHERE runStartTime > :required_time")
    List<RunTableItem> listRecent(long required_time);

    @Insert
    void insert(RunTableItem runTableItem);

    @Delete
    void delete(RunTableItem runTableItem);

    @Query("DELETE FROM run_history")
    void deleteAll();
}
