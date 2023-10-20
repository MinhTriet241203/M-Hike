package com.example.m_hike;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface HikeDao {

    @Insert
    void insertHike(Hike hike);

}
