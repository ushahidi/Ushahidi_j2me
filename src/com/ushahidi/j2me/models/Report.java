package com.ushahidi.j2me.models;

import org.json.me.JSONException;

/**
 * Report Model
 * @author dalezak
 */
public class Report extends Model {

    public Report() {
        super();
    }

    protected Report(String json) throws JSONException {
        super(json);
    }

    public static Report load(String path) {
        try {
            return new Report(fromString(path));
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean save() {
        return save("file:///" + getDefaultRoot() + getID() + ".txt");
    }

    public int getID() {
        try {
            return getInt("id");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void setID(int id) {
        try {
            put("id", id);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getDate() {
        try {
            return getString("date");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setDate(String date) {
        try {
            put("date", date);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getTitle() {
        try {
            return getString("title");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setTitle(String title) {
        try {
            put("title", title);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getLocation() {
        try {
            return getString("description");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setLocation(String location) {
        try {
            put("location", location);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getDescription() {
        try {
            return getString("description");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setDescription(String description) {
        try {
            put("description", description);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
//
//    public Image getMap() {
//        return map;
//    }
//
//    public void setMap(Image map) {
//        this.map = map;
//    }
//
//    public Image[] getPhotos() {
//        return photos;
//    }
//
//    public void setPhotos(Image[] photos) {
//        this.photos = photos;
//    }
//
//    public Image getPhoto(int index) {
//        return photos != null && photos.length > index ? photos[index] : null;
//    }
//
//    public int getPhotoCount() {
//        return 0;
//    }

//    public String getCoordinates() {
//        return latitude != null && longitude != null ? latitude + "," + longitude : null;
//    }

    public String getLatitude() {
        try {
            return getString("latitude");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setLatitude(String latitude) {
        try {
            put("latitude", latitude);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getLongitude() {
        try {
            return getString("longitude");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setLongitude(String longitude) {
        try {
            put("longitude", longitude);
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
