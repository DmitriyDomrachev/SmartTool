package com.uraldroid.dima.smarttool;

/**
 * Created by dima on 18.04.2018.
 */

public class Note {
    private String text, name;
    private int id;
    private double lat, lng;
    private long startTime;

    public Note( int id, String name, String text,double lat, double lng, long startTime) {
        this.text = text;
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", startTime='" + startTime + '\'' +
                '}'+"\n";
    }
}
