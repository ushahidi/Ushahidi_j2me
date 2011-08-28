package com.ushahidi.j2me.models;

import com.sun.lwuit.Image;
import java.util.Date;

/**
 * Report Model
 * @author dalezak
 */
public class Report extends Base {

    private int id;
    private Date date;
    private String title;
    private String location;
    private String description;
    private String latitude;
    private String longitude;
    private Image map;
    private Image[] photos;

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

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return date != null ? date.toString() : null;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getMap() {
        return map;
    }

    public void setMap(Image map) {
        this.map = map;
    }

    public Image[] getPhotos() {
        return photos;
    }

    public void setPhotos(Image[] photos) {
        this.photos = photos;
    }

    public Image getPhoto(int index) {
        return photos != null && photos.length > index ? photos[index] : null;
    }

    public int getPhotoCount() {
        return 0;
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
