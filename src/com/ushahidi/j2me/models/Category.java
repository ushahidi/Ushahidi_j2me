package com.ushahidi.j2me.models;

import org.json.me.JSONException;

/**
 *
 * @author dalezak
 */
public class Category extends Model {

    private int id;
    private String name;
    private String description;

    protected Category(String json) throws JSONException {
        super(json);
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
