package com.example.m_hike;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ObservationDao {

    @Insert
    void insertObservation(Observation observation);

    @Query("SELECT * FROM observations WHERE hikeId = :hike_id")
    LiveData<Observation> getObservationForHike(int hike_id);

    @Update
    void updateObservation(Observation observation);

    @Delete
    void deleteObservation(Observation observation);

}
