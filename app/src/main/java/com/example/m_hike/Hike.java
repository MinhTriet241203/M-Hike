package com.example.m_hike;

import java.sql.Date;

public class Hike {
    String description, image, date, location, name;
    Integer difficulty;
    Float lat, lng, length;
    Boolean parking;

    Hike(){}

    public Hike(String description, String image, String date, String location, String name, Integer difficulty, Float lat, Float lng, Float length, Boolean parking) {
        this.description = description;
        this.image = image;
        this.date = date;
        this.location = location;
        this.name = name;
        this.difficulty = difficulty;
        this.lat = lat;
        this.lng = lng;
        this.length = length;
        this.parking = parking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }
}
