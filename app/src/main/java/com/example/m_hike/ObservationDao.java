package com.example.m_hike;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ObservationDao {

    @Insert
    long insertObservation(Observation observation);

    @Query("SELECT * FROM observations WHERE hikeId = :hike_id")
    LiveData<List<Observation>> getObservationsForHike(int hike_id);

    @Query("SELECT * FROM observations WHERE observation_id = :observation_id")
    Observation getObservationById(int observation_id);

    @Query("SELECT DISTINCT hikeId FROM observations WHERE observation_type LIKE :observation_type")
    LiveData<List<Integer>> getHikeIdByObservationType(String observation_type);

    @Delete
    void deleteObservation(Observation observation);

}
