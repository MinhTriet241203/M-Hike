package com.example.m_hike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "hikes", indices = {@Index(value = {"hike_name"}, unique = true)})
public class Hike {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hike_id")
    private int hikeId;

    @ColumnInfo(name = "hike_name")
    private String hikeName;

    @ColumnInfo(name = "hike_location")
    private String location;

    @ColumnInfo(name = "hike_date")
    private String date;

    @ColumnInfo(name = "hike_parking")
    private Boolean parking;

    @ColumnInfo(name = "hike_length")
    private Double length;

    @ColumnInfo(name = "hike_difficulty")
    private Integer difficulty;

    @ColumnInfo(name = "hike_description")
    @Nullable
    private String description;

    @ColumnInfo(name = "hike_equipments")
    @Nullable
    private String equipments;

    @ColumnInfo(name = "hike_participants")
    private Integer participants;

    @ColumnInfo(name = "hike_duration")
    private Double duration;

    public Hike(String hikeName, String location, String date, Boolean parking, Double length, Integer difficulty, @Nullable String description, @Nullable String equipments, Integer participants, Double duration) {
        this.hikeName = hikeName;
        this.location = location;
        this.date = date;
        this.parking = parking;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.equipments = equipments;
        this.participants = participants;
        this.duration = duration;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public String getHikeName() {
        return hikeName;
    }

    public void setHikeName(String hikeName) {
        this.hikeName = hikeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getEquipments() {
        return equipments;
    }

    public void setEquipments(@Nullable String equipments) {
        this.equipments = equipments;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "Hike{" +
                "hikeId=" + hikeId +
                ", hikeName='" + hikeName + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", parking=" + parking +
                ", length=" + length +
                ", difficulty=" + difficulty +
                ", description='" + description + '\'' +
                ", equipments='" + equipments + '\'' +
                ", participants=" + participants +
                ", duration=" + duration +
                '}';
    }
}
