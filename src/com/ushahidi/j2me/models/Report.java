package com.ushahidi.j2me.models;

import com.sun.lwuit.Image;

/**
 * Report Model
 * @author dalezak
 */
public class Report extends Base {

    public boolean load() {
        return false;
    }

    public boolean save() {
        return false;
    }

    public String getDate() {
        return null;
    }

    public String getTitle() {
        return null;
    }
    
    public String getLocation() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public Image getMap() {
        return null;
    }

    public Image[] getPhotos() {
        return new Image[0];
    }

    public Image getPhoto(int index) {
        return null;
    }

    public int getPhotoCount() {
        return 0;
    }

    public String getCoordinates() {
        return null;
    }

    public String getLatitude() {
        return null;
    }

    public String getLongitude() {
        return null;
    }

}
