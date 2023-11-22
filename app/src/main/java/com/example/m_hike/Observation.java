package com.example.m_hike;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "observations", foreignKeys = @ForeignKey(entity = Hike.class, parentColumns = "hike_id", childColumns = "hikeId", onDelete = 5, onUpdate = 5),
        indices = @Index(value = {"hikeId"}))
public class Observation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "observation_id")
    private int observationId;

    @ColumnInfo(name = "observation_type")
    private String observationType;

    @ColumnInfo(name = "observation_time")
    private String observationTime;

    @ColumnInfo(name = "observation_comment")
    @Nullable
    private String observationComment;

    @ColumnInfo(name = "observation_image")
    @Nullable
    private String observationImage;

    @ColumnInfo(name = "longitude")
    private double observationLongitude;

    @ColumnInfo(name = "latitude")
    private double observationLatitude;

    @ColumnInfo(name = "hikeId")
    private int hikeId;

    public Observation(String observationType, String observationTime, @Nullable String observationComment, @Nullable String observationImage, double observationLongitude, double observationLatitude, int hikeId) {
        this.observationType = observationType;
        this.observationTime = observationTime;
        this.observationComment = observationComment;
        this.observationImage = observationImage;
        this.observationLongitude = observationLongitude;
        this.observationLatitude = observationLatitude;
        this.hikeId = hikeId;
    }

    public int getObservationId() {
        return observationId;
    }

    public void setObservationId(int observationId) {
        this.observationId = observationId;
    }

    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }

    @Nullable
    public String getObservationComment() {
        return observationComment;
    }

    public void setObservationComment(@Nullable String observationComment) {
        this.observationComment = observationComment;
    }

    @Nullable
    public String getObservationImage() {
        return observationImage;
    }

    public void setObservationImage(@Nullable String observationImage) {
        this.observationImage = observationImage;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public double getObservationLongitude() {
        return observationLongitude;
    }

    public void setObservationLongitude(double observationLongitude) {
        this.observationLongitude = observationLongitude;
    }

    public double getObservationLatitude() {
        return observationLatitude;
    }

    public void setObservationLatitude(double observationLatitude) {
        this.observationLatitude = observationLatitude;
    }
}