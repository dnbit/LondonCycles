
package com.dnbitstudio.londoncycles.model;

public class BikePoint {

    private String id;
    private String name;
    private double lat;
    private double lon;
    private int docks;
    private int empty;
    private int bikes;

    public BikePoint(String id, String name, double lat, double lon, int docks, int empty, int bikes) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.docks = docks;
        this.empty = empty;
        this.bikes = bikes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getDocks() {
        return docks;
    }

    public void setDocks(int docks) {
        this.docks = docks;
    }

    public int getEmpty() {
        return empty;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }
}
