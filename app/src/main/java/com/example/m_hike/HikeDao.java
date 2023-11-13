package com.example.m_hike;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HikeDao {

    @Insert
    long insertHike(Hike hike);

    @Query("SELECT * FROM hikes")
    LiveData<List<Hike>> getAllHikes();

    @Query("SELECT * FROM hikes WHERE hike_id = :hike_id")
    LiveData<Hike> getHikeById(int hike_id);

    @Query("SELECT * FROM hikes WHERE hike_name LIKE :hike_name")
    LiveData<List<Hike>> getHikeByName(String hike_name);

    @Query("SELECT * FROM hikes WHERE hike_location LIKE :hike_location")
    LiveData<List<Hike>> getHikeByLocation(String hike_location);

    @Query("SELECT * FROM hikes WHERE hike_length LIKE :hike_length")
    LiveData<List<Hike>> getHikeByLength(String hike_length);

    @Query("SELECT * FROM hikes WHERE hike_date LIKE :hike_date")
    LiveData<List<Hike>> getHikeByDate(String hike_date);

    @Query("SELECT * FROM hikes WHERE hike_id IN (:ids)")
    LiveData<List<Hike>> getHikesById(List<Integer> ids);

    @Update
    void updateHike(Hike hike);

    @Delete
    void deleteHike(Hike hike);

    @Query("DELETE FROM hikes")
    void resetDatabase();

}
