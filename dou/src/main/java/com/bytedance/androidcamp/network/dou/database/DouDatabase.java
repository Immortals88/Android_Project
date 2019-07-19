package com.bytedance.androidcamp.network.dou.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class DouDatabase extends RoomDatabase {
    private static final String DatabaseName = "dou.db";
    private static DouDatabase instance;

    public static DouDatabase getDatabase(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext()
                    , DouDatabase.class, DatabaseName).allowMainThreadQueries().build();
        }
        return instance;
    }

    public static void onDestroy() {
        instance = null;
    }

    public abstract UserEntityDao getUserEntityDao();
}
