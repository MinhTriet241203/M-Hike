package com.example.m_hike;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Hike.class}, version = 1, exportSchema = false)
public abstract class MHikeDatabase extends RoomDatabase {

    public abstract HikeDao hikeDao();

    private static volatile MHikeDatabase INSTANCE;

    static MHikeDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (MHikeDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MHikeDatabase.class, "M-Hike").build();
                }
            }
        }
        return INSTANCE;
    }
}