package com.traindelaythegame.models;

public class Cords {
    private double lat;
    private double lon;

    public Cords(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
