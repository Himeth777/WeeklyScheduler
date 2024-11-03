package org.example;

import java.io.Serializable;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Serializable{
    private String name;
    private String location;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Color color;

    public Event(String name, String location, LocalDate date, LocalTime startTime, LocalTime endTime, Color color) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Color getColor() {
        return color;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

