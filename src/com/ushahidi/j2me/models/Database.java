package com.ushahidi.j2me.models;

/**
 * Database
 * @author dalezak
 */
public class Database {

    public static Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }private static Database database;

    public int getReportCount() {
        return 0;
    }

    public Report getReport(int index) {
        return null;
    }

    public Report[] getReports() {
        return new Report[0];
    }

    public Report[] getReports(Category category) {
        return new Report[0];
    }

    public String[] getReportNames() {
        return new String[0];
    }

    public String[] getReportNames(Category category) {
        return new String[0];
    }

    public int getCategoryCount() {
        return 0;
    }

    public Category getCategory(int index) {
        return null;
    }
    
    public Category[] getCategories() {
        return new Category[0];
    }

    public String[] getCategoryNames() {
        return new String[0];
    }


    public int getLocationCount() {
        return 0;
    }

    public Location getLocation(int index) {
        return null;
    }

    public Location[] getLocations() {
        return new Location[0];
    }

    public String[] getLocationNames() {
        return new String[0];
    }

}
