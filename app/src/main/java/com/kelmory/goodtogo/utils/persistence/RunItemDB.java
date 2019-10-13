package com.kelmory.goodtogo.utils.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RunTableItem.class}, version = 1, exportSchema = false)
public abstract class RunItemDB extends RoomDatabase {
    private static final String DATABASE_NAME = "runitem_db";
    private static RunItemDB DB_INSTANCE;

    public abstract RunItemDAO toDoItemDao();

    public static RunItemDB getDatabase(Context context) {
        if (DB_INSTANCE == null) {
            synchronized (RunItemDB.class) {
                DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        RunItemDB.class, DATABASE_NAME).build();
            }
        }
        return DB_INSTANCE;
    }

    public static void destroyInstance() {
        DB_INSTANCE = null;
    }
}
