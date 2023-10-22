package com.example.m_hike;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HikeDao {

    @Insert
    void insertHike(Hike hike);

    @Query("SELECT * FROM hikes")
    List<Hike> getAllHikes();

}
