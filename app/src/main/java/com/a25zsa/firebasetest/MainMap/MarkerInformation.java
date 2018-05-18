package com.a25zsa.firebasetest.MainMap;

/**
 * Created by 25zsa on 4/15/2018.
 */

public class MarkerInformation {
    private double lat;
    private double lng;
    private String hashNumber;

    public MarkerInformation(double lat, double lng, String hashNumber){
        this.lat = lat;
        this.lng = lng;
        this.hashNumber = hashNumber;
    }

    public String getHashNumber(){
        return hashNumber;
    }

    public String getLat(){
        return Double.toString(lat);
    }

    public String getLng(){
        return Double.toString(lng);
    }
}
