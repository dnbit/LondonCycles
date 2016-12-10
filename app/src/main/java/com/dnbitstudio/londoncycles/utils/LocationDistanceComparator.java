package com.dnbitstudio.londoncycles.utils;

import com.dnbitstudio.londoncycles.model.BikePoint;

import java.util.Comparator;

public class LocationDistanceComparator implements Comparator<BikePoint> {
    private double latitude;
    private double longitude;

    public LocationDistanceComparator(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int compare(BikePoint bikePoint1, BikePoint bikePoint2) {
        double distance1 = DistanceCalculator
                .distance(bikePoint1.getLat(), bikePoint1.getLon(), latitude, longitude);
        double distance2 = DistanceCalculator
                .distance(bikePoint2.getLat(), bikePoint2.getLon(), latitude, longitude);
        if (distance1 < distance2) {
            return -1;
        } else if (distance1 > distance2) {
            return 1;
        }
        return 0;
    }
}
