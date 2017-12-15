package com.cevnyne.trackids.models;


public class Position {

    private Double lat;
    private Double lng;

    public Position() {
        this(0.0, 0.0);
    }

    public Position(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

}
