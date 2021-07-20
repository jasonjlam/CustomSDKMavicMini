package com.dji.customsdk;

import android.location.Location;

import java.util.ArrayList;

import dji.common.flightcontroller.LocationCoordinate3D;

public class WaypointNavigation {
    private ArrayList<LocationCoordinate3D> waypoints;
    public int currentWaypoint;

    public enum Mode {
        SQUARE;
    }

    public WaypointNavigation(Mode mode, double latitude, double longitude, float altitude) {
        switch (mode) {
            case SQUARE:
                addWaypoint(latitude, longitude, altitude);
                addWaypoint(latitude + 0.0001, longitude, altitude);
                addWaypoint(latitude + 0.0001, longitude + 0.0001, altitude);
                addWaypoint(latitude , longitude + 0.0001, altitude);
                addWaypoint(latitude - 0.0001, longitude + 0.0001, altitude);
                addWaypoint(latitude - 0.0001, longitude, altitude);
                addWaypoint(latitude - 0.0001, longitude - 0.0001, altitude);
                addWaypoint(latitude , longitude - 0.0001, altitude);
                addWaypoint(latitude + 0.0001, longitude - 0.0001, altitude);
                addWaypoint(latitude + 0.0001, longitude, altitude);
                addWaypoint(latitude, longitude, altitude);
                break;
        }
        currentWaypoint = 1;

    }

    public int numWaypoints() {
        return waypoints.size();
    }
    public void addWaypoint(double latitude, double longitude, float altitude) {
        waypoints.add(new LocationCoordinate3D(latitude, longitude, altitude));
    }

    public void insertWaypoint(double latitude, double longitude, float altitude) {
        waypoints.add(new LocationCoordinate3D(latitude, longitude, altitude));
        currentWaypoint++;
    };

    public void removeWaypoint(int index) {
        waypoints.remove(index);
    }

    public boolean hasNextHeading() {
        return currentWaypoint < waypoints.size();
    }

    public float getNextHeading(double lat1, double long1) {
        double lat2 = waypoints.get(currentWaypoint).getLatitude();
        double long2 = waypoints.get(currentWaypoint).getLongitude();
//        currentWaypoint++;
        double x = Math.cos(lat2) * Math.sin(long2 - long1);
        double y = Math.cos(lat1) * Math.sin(lat2)
                 - Math.sin(lat1) * Math.cos(lat2) * Math.cos(long2 - long1);
        return (float)Math.atan2(x, y);
    }

    public boolean isCloseToWaypoint(double lat1, double long1) {
        double lat2 = waypoints.get(currentWaypoint).getLatitude();
        double long2 = waypoints.get(currentWaypoint).getLongitude();
        boolean closeToWayPoint = Math.abs(long2 - long1) < 0.00001
                               && Math.abs(lat2 - lat1) < 0.00001;
        if (closeToWayPoint) {
            currentWaypoint++;
        }
        return closeToWayPoint;
    }

    public boolean isOvershootingWaypoint(double lat1, double long1){
        double latCalculatedDelta = waypoints.get(currentWaypoint).getLatitude()
                        - waypoints.get(currentWaypoint - 1).getLatitude();
        double longCalculatedDelta= waypoints.get(currentWaypoint).getLongitude()
                        - waypoints.get(currentWaypoint - 1).getLongitude();
        double latActualDelta = lat1 - waypoints.get(currentWaypoint).getLatitude();
        double longActualDelta = long1 - waypoints.get(currentWaypoint).getLongitude();
        return latActualDelta * latCalculatedDelta < 0 || longActualDelta * longCalculatedDelta < 0;
    }
}