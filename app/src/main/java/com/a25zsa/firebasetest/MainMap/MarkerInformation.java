package com.a25zsa.firebasetest.MainMap;

/**
 * Created by 25zsa on 4/15/2018.
 */

/**
 * this class contains the marker information
 */
public class MarkerInformation {
    private double lat;
    private double lng;
    private String hashNumber;

    /**
     * Instantiates a new Marker information.
     *
     * @param lat        the latitude
     * @param lng        the longitude
     * @param hashNumber the hash number
     */
    public MarkerInformation(double lat, double lng, String hashNumber){
        this.lat = lat;
        this.lng = lng;
        this.hashNumber = hashNumber;
    }

    /**
     * Get hash number string.
     *
     * @return the string
     */
    public String getHashNumber(){
        return hashNumber;
    }

    /**
     * Get latitude string.
     *
     * @return the string
     */
    public String getLat(){
        return Double.toString(lat);
    }

    /**
     * Get longitude string.
     *
     * @return the string
     */
    public String getLng(){
        return Double.toString(lng);
    }
}
