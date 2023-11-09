package com.example.m_hike;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HikeDao {

    @Insert
    void insertHike(Hike hike);

    @Query("SELECT * FROM hikes")
    LiveData<List<Hike>> getAllHikes();

    @Query("SELECT * FROM hikes WHERE hike_id = :hike_id")
    LiveData<Hike> getHikeById(int hike_id);

    @Update
    void updateHike(Hike hike);

    @Delete
    void deleteHike(Hike hike);

    @Query("DELETE FROM hikes")
    void resetDatabase();

}
