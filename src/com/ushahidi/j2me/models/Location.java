package com.ushahidi.j2me.models;

/**
 *
 * @author dalezak
 */
public class Location extends Base {

    private int id;
    private String name;
    private String latitude;
    private String longitude;

    public boolean load() {
        return false;
    }

    public boolean save() {
        return false;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinates() {
        return latitude != null && longitude != null ? latitude + "," + longitude : null;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
