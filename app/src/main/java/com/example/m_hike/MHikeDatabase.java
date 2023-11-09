package com.example.m_hike;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Hike.class, Observation.class}, version = 1, exportSchema = false)
public abstract class MHikeDatabase extends RoomDatabase {

    public abstract HikeDao hikeDao();
    public abstract ObservationDao observationDao();

    private static volatile MHikeDatabase INSTANCE;

    public static synchronized MHikeDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (MHikeDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MHikeDatabase.class, "M-Hike")
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}