/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * @author stuart
 */
public class UshahidiInstance implements Runnable {

    public void run() {
    }
   
    /**
     *Tests data connection availability by connecting to the last used
     * Ushahidi instance. Uses http://demo.ushahidi.com for the first time use
     * and current instance for subsequent tests
     * @return true if a HTTP_CODE 200 is returned and false otherwise
     */
    public int isConnectionAvailable() {
        int connectionStatus = 0;

        try {            
            instanceConnection = (HttpConnection) Connector.open(getUshahidiInstance());
            instanceConnection.setRequestMethod(HttpConnection.HEAD);
            connectionStatus = instanceConnection.getResponseCode();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        } //end finally

        return connectionStatus;
    }

    /**
     * Sets the current instance address
     *
     * @param currentInstance
     */
    public static void setUshahidiInstance(String currentInstance) {
        UshahidiInstance.currentInstance = currentInstance;
    }

    /**
     * Returns active Ushahidi instance address
     *
     * @return URL of current/active Ushahidi instance
     */
    public static String getUshahidiInstance() { return currentInstance; }    

    /**
     * This method submits reports to an instance of an Ushahidi engine based on
     * the data collected from a user-filled form and the settings information
     * 
     * @param incident_title
     * @param incident_description
     * @param incident_date
     * @param incident_location
     * @param incident_category
     * @return if the report was saved successfully(i.e. true / false)
     */
    //<editor-fold defaultstate="collapsed" desc="Submit report">
    public boolean submitIncident(String incident_title, String incident_description, String[] incident_date, String incident_location, String incident_category) {
        DataOutputStream dataOutputStream = null;
        String success = "";
        double[] geoCoordinates = null;

        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api") : ushahidiInstance.concat("/api");
        String [] setting = (new UshahidiSettings()).getSettings();        
        String data = "";

        // Retrieve Geographical co-ordianates i.e. latitude and longitude
        try {
            geoCoordinates = (new Gmapclass(ushahidi.Ushahidi.getMapApiKey())).geocodeAddress(incident_location);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }


        // Prepare the data to be sent to the server
        String[] params = {
            "task=report",
            "&incident_title="+incident_title,
            "&incident_description="+incident_description,
            "&incident_date="+getIncidentDate(incident_date),
            "&incident_hour="+getHour(incident_date[3]),
            "&incident_minute="+getMinutes(incident_date[3]),
            "&incident_ampm="+getAmPm(incident_date[3]),
            "&incident_category="+incident_category,
            "&latitude="+geoCoordinates[0],
            "&longitude="+geoCoordinates[1],
            "&location_name="+incident_location,
            "&person_first="+setting[2],
            "&person_last="+setting[3],
            "&person_email="+setting[4],
            "&resp=xml"
        };

        // Concatenate the parameters and their values to a string
        for ( int i = 0; i < params.length; i ++ ) {
            data = data.concat(params[i]);
        }

        // Replace any white spaces with the '+' character
        data = data.replace(' ', '+');

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            instanceConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
            instanceConnection.setRequestProperty("Connection", "keep-alive");
            instanceConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // multipart/form-data
            instanceConnection.setRequestProperty("Content-Length", data.getBytes().length+"");
            instanceConnection.setRequestMethod(HttpConnection.POST);
            dataOutputStream = instanceConnection.openDataOutputStream();
            byte[] byteData = data.getBytes();

            // Send the data byte byte
            for ( int i = 0; i < byteData.length ; i++ ) {
                dataOutputStream.write(byteData[i]);
            }

            // process server response
            Reader reader = null;
            try {
                reader = new InputStreamReader(instanceConnection.openInputStream());
                KXmlParser parser = new KXmlParser();
                parser.setInput(reader);
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "response");

                while(parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG && "success".equals(parser.getName()))
                        success = parser.nextText();
                }
                
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                if ( reader != null ) reader.close();
            }

            // Close the DataOutputStream if still open
            if (dataOutputStream != null) dataOutputStream.close();  // Close DataOutputStream
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {            
            closeHttpConnection();
        } //end finally

        return (success.equals("true"))? true : false;
    }
    //</editor-fold>

    private String getIncidentDate(String[] rawDate) {
        String[] monthsOfYear = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int monthNumber = -1;

        for (int i = 0; i < monthsOfYear.length; i++ ) {
            if (rawDate[1].equals(monthsOfYear[i])) monthNumber = i + 1;
        } // end for

        return String.valueOf(monthNumber).concat("/").concat(rawDate[2]).concat("/").concat(rawDate[4]);
    }

    private String getHour(String time) {
        String hour = null;
        String hr = time.substring(0, time.indexOf(":", 1));

        if ( Integer.parseInt(hr) > 12 ) hour = String.valueOf(Integer.parseInt(hr) - 12);
        else if ( Integer.parseInt(hr) == 0 ) hour = "12";
        else hour = hr;
                
        return hour;
    }

    private String getMinutes(String time) {
        return time.substring(time.indexOf(":", 1) + 1, time.indexOf(":", 4));
    }

    private String getAmPm(String time) {
        String hour = time.substring(0, time.indexOf(":", 1));        
        return ( (Integer.parseInt(hour) >= 12) && (Integer.parseInt(hour) <= 23) )? "pm": "am";
    }

    public String getApiKey(String maps) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=apikeys&by="+maps+"&resp=xml") : ushahidiInstance.concat("/api?task=apikeys&by=google&resp=xml");
        String key = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            KXmlParser parser = new KXmlParser();
            parser.setInput(new InputStreamReader(instanceConnection.openInputStream()));
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    
                    if (parser.getName().equals("apikey"))
                        key = parser.nextText();
                }
            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
        
        return key;
    }

    /**
     * Retries all categories available in an Ushahidi instance
     *
     * @return An XML with the categories in ascending order
     */
    //<editor-fold defaultstate="collapsed" desc="Get Categories">
    public UshahidiInstance getCategories() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=categories&resp=xml") : ushahidiInstance.concat("/api?task=categories&resp=xml");
        instanceCategories = new Vector();
        String id = null, title = null, desc = null, color = null;
        
        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("category"))
                        parser.nextTag();

                    if (parser.getName().equals("id"))
                        id = parser.nextText();

                    if (parser.getName().equals("title"))
                        title = parser.nextText();

                    if (parser.getName().equals("description"))
                        desc = parser.nextText();

                    if (parser.getName().equals("color")) {
                        color = parser.nextText();
                        instanceCategories.addElement(new String[] {id, title, desc, color});
                    }
                    
                }
            }
                
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        } while(isFetching());

        return this;
    }
    //</editor-fold>

    public String[] getTitles(int fieldIndex) {
        String[] titles = null;

        Vector categories = this.instanceCategories;
        titles = new String[categories.size()];
        
        for ( int i = 0; i < categories.size(); i++ ) {
            String[] category = (String[]) categories.elementAt(i);
            titles[i] = category[fieldIndex];
        }

        return titles;
    }

    public String getCategoryById(int id) {
        // Handles the forward slash at the end of the instance.
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=categories&by="+id+"&resp=xml") : ushahidiInstance.concat("/api?task=categories&by="+id+"&resp=xml");
        String categories = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("category".equals(parser.getName())) parser.next();
                        if("id".equals(parser.getName())) {
                            if ( categories == null)categories = parser.nextText();
                            else categories += parser.nextText();
                        } else if("title".equals(parser.getName())) categories += parser.nextText();
                        else if("description".equals(parser.getName())) categories += parser.nextText();
                        else if("color".equals(parser.getName())) categories += parser.nextText();
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
        return categories;
    }

    public Vector getCountries() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=countries&resp=xml") : ushahidiInstance.concat("/api?task=countries&resp=xml");
        String id = null, iso = null, name = null, capital = null;
        Vector countryVector = new Vector();

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {

                    if ("country".equals(parser.getName()))
                        parser.next();

                        if ("id".equals(parser.getName()))
                            id = parser.nextText();

                        if("iso".equals(parser.getName()))
                            iso  = parser.nextText();

                        if("name".equals(parser.getName()))
                            name  = parser.nextText();

                        if("capital".equals(parser.getName())) {
                            capital  = parser.nextText();
                            countryVector.addElement(new String[] {id, iso, name, capital});
                        }
                }

            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
        return countryVector;
    }

    public void getCountryById(int id) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=country&by="+id+"&resp=xml") : ushahidiInstance.concat("/api?task=country&by="+id+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getCoutryByISO(String iso) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=country&by="+iso+"&resp=xml") : ushahidiInstance.concat("/api?task=country&by="+iso+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getCountryByName(String countryName) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=country&by="+countryName+"&resp=xml") : ushahidiInstance.concat("/api?task=country&by="+countryName+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getLocations() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=locations&resp=xml") : ushahidiInstance.concat("/api?task=locations&resp=xml");
        String locations = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("location".equals(parser.getName())) parser.next();
                        if("id".equals(parser.getName())) {
                            if (locations == null) locations = parser.nextText();
                            else locations += parser.nextText();
                        } else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                        else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                        else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getLocationById(int locId) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=locations&by="+locId+"&resp=xml") : ushahidiInstance.concat("/api?task=locations&by="+locId+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getLocationByCountryId(int countryId) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=locations&by="+countryId+"&resp=xml") : ushahidiInstance.concat("/api?task=locations&by="+countryId+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public Vector getIncidents() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=all&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=all&resp=xml");
        String id = null, title = null, description = null, date = null, mode = null, active = null, verified = null;
        Vector incidentsVector = new Vector();

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "payload");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "incidents");
            parser.nextTag();

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {

                    if (parser.getName().equals("incident"))
                        parser.nextTag();

                    if (parser.getName().equals("id"))
                        id = parser.nextText();

                    if (parser.getName().equals("title"))
                        title = parser.nextText();

                    if (parser.getName().equals("description"))
                        description = parser.nextText();

                    if (parser.getName().equals("date"))
                        date = parser.nextText();

                    if (parser.getName().equals("mode"))
                        mode = parser.nextText();

                    if (parser.getName().equals("active"))
                        active = parser.nextText();

                    if (parser.getName().equals("verified"))
                        verified = parser.nextText();

                    // Location info
                    if (parser.getName().equals("location"))
                        parser.skipSubTree();
//
                    if (parser.getName().equals("categories"))
                        parser.skipSubTree();

                    if (parser.getName().equals("mediaItems")) {
                        incidentsVector.addElement(new String[] {id, title, description, date, mode, active, verified});
                    }

                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        return incidentsVector;
    }

//    public String getIncidentsByCategoryId(int categoryId) {
//        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
//        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=catid&id="+categoryId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=catid&id="+categoryId+"&resp=xml");
//        String incidents = null;
//
//        try {
//            instanceConnection = (HttpConnection) Connector.open(url);
//            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
//            KXmlParser parser = new KXmlParser();
//            parser.setInput(reader);
//            parser.nextTag();
//            parser.require(XmlPullParser.START_TAG, null, "response");
//
//            while (parser.next() != XmlPullParser.END_DOCUMENT) {
//                if(parser.getEventType() == XmlPullParser.START_TAG) {
//                    if("incident".equals(parser.getName())) parser.next();
//                        if("id".equals(parser.getName())) {
//                            if (incidents == null) incidents = parser.nextText() + "|";
//                            else incidents += parser.nextText() + "|";
//                        }
//                        else if("title".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("description".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("date".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("mode".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("active".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("verified".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("location".equals(parser.getName())) parser.next(); // Location
//                        else if("id".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("name".equals(parser.getName())) incidents += parser.nextText() + "|";
//                        else if("latitude".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("longitude".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("categories".equals(parser.getName())) parser.next(); //Categories
//                        else if("category".equals(parser.getName())) parser.next();
//                        else if("id".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("title".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("verified".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("description".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("date".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("mode".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("active".equals(parser.getName())) System.out.println(parser.nextText());
//                        else if("verified".equals(parser.getName())) System.out.println(parser.nextText());
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        } finally {
//            closeHttpConnection();
//        }
//
//        return incidents;
//    }

    public Vector getIncidentsByCategoryName(String categoryName) {
        // To handle xml errors
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        categoryName = categoryName.replace(' ', '+');
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=catname&name="+categoryName+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=catname&name="+categoryName+"&resp=xml");
        System.out.println(url);
        String id = null, title = null, description = null, date = null, mode = null, active = null, verified = null;
        Vector incidentsVector = new Vector();

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {

                    if (parser.getName().equals("incident"))
                        parser.nextTag();

                    if (parser.getName().equals("id"))
                        id = parser.nextText();

                    if (parser.getName().equals("title"))
                        title = parser.nextText();

                    if (parser.getName().equals("description"))
                        description = parser.nextText();

                    if (parser.getName().equals("date"))
                        date = parser.nextText();

                    if (parser.getName().equals("mode"))
                        mode = parser.nextText();

                    if (parser.getName().equals("active"))
                        active = parser.nextText();

                    if (parser.getName().equals("verified"))
                        verified = parser.nextText();

                    // Location info
                    if (parser.getName().equals("location"))
                        parser.skipSubTree();
                    
                     // MediaItems is not present in the XML file and shall thus be removed
//                    if (parser.getName().equals("mediaItems")) {
                    if (parser.getName().equals("categories")) {
                        incidentsVector.addElement(new String[] {id, title, description, date, mode, active, verified});
                    }

                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
//        setCategoryIncidents(categoryIncidents);

        return incidentsVector;
    }

//    public static void setCategoryIncidents(Vector categoryIncidents) {
//        UshahidiInstance.categoryIncidents = categoryIncidents;
//    }
//
//    public static Vector getCategoryIncidents() {
//        return categoryIncidents;
//    }

//    public String[] getIncidentByTitle(String incidentTitle) {
//        Vector categoryIncidents = getCategoryIncidents();
//        String[] targetIncident = null;
//
//        for (int i = 0; i < categoryIncidents.size(); i++) {
//            String[] incident = (String[]) categoryIncidents.elementAt(i);
//            if (incidentTitle.equals(incident[1]))
//                targetIncident = incident;
//        }
//
//        return targetIncident;
//    }

    public void getIncidentsByLocationId(int locationId) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=locid&id="+locationId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=locid&id="+locationId+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {

                    if("id".equals(parser.getName())) 
                        System.out.println(parser.nextText());

                    if("title".equals(parser.getName())) 
                        System.out.println(parser.nextText());

                    if("description".equals(parser.getName()))
                        System.out.println(parser.nextText());

                    if("color".equals(parser.getName()))
                        System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    public void getIncidentsByLocationName(int locationName) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=locname&name="+locationName+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by="+locationName+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

     public void getIncidentsBySinceId(int sinceId) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=sinceid&id="+sinceId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=sinceid&id="+sinceId+"&resp=xml");
        String id = null, title = null, description = null, date = null, mode = null, active = null, verified = null;
        Vector incidentsVector = new Vector();

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "payload");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "incidents");
            parser.nextTag();

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {

                    if (parser.getName().equals("incident"))
                        parser.nextTag();

                    if (parser.getName().equals("id"))
                        id = parser.nextText();

                    if (parser.getName().equals("title"))
                        title = parser.nextText();

                    if (parser.getName().equals("description"))
                        description = parser.nextText();

                    if (parser.getName().equals("date"))
                        date = parser.nextText();

                    if (parser.getName().equals("mode"))
                        mode = parser.nextText();

                    if (parser.getName().equals("active"))
                        active = parser.nextText();

                    if (parser.getName().equals("verified"))
                        verified = parser.nextText();

                    // Location info
                    if (parser.getName().equals("location"))
                        parser.skipSubTree();
//
                    if (parser.getName().equals("categories"))
                        parser.skipSubTree();

                    if (parser.getName().equals("mediaItems")) {
                        incidentsVector.addElement(new String[] {id, title, description, date, mode, active, verified});
                    }

                }
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    /**
     *  Retrieves number of approved Ushahidi instances from  an instance of
     * the Ushahidi engine.
     * 
     * @return Number of approved incident reports
     */
    public int getIncidentCount() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidentcount&resp=xml") : ushahidiInstance.concat("/api?task=incidentcount&resp=xml");
        String incidentCount = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("count".equals(parser.getName())) incidentCount = parser.nextText();
                    else if("code".equals(parser.getName())) {
                        if (! parser.nextText().equals("0")) {
                            incidentCount = null;
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        return Integer.parseInt(incidentCount);
    }

   public String getGeographicMidpoint() {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=geographicmidpoint&resp=xml") : ushahidiInstance.concat("/api?task=geographicmidpoint&resp=xml");
        String coordinate = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("geographic_midpoint".equals(parser.getName())) parser.next();
                        if ("latitude".equals(parser.getName()))
                            coordinate  = parser.nextText() + "|";
                        else if("longitude".equals(parser.getName()))
                            coordinate  += parser.nextText();
                }

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        return coordinate;
    }

    public void getIncidentsOrderBy(String fieldName) {
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&orderfield="+fieldName+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&orderfield="+fieldName+"&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("id".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("title".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("description".equals(parser.getName())) System.out.println(parser.nextText());
                    else if("color".equals(parser.getName())) System.out.println(parser.nextText());
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    /**
     * Retrieves version number of Ushahidi engine in use
     *
     * @return version number
     */
    public String getVersion() {
        String version = null;
        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=version&resp=xml") : ushahidiInstance.concat("/api?task=version&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("version".equals(parser.getName()))
                        if ( (parser.nextTag() == XmlPullParser.START_TAG) && ("version".equals(parser.getName())) )
                            version = parser.nextText();
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        return version;
    }

    private void closeHttpConnection() {
        try {
            if (instanceConnection != null) instanceConnection.close();
        } catch (Exception e) {
            //Exception handler here
            System.err.println(e.getMessage());
        }
    }

    private void setFetching(boolean fetching) {
        UshahidiInstance.fetching = fetching;
    }

    private boolean isFetching() { return fetching; }

    private static Vector categoryIncidents = null;
    private static boolean fetching = false;
    private Vector instanceCategories = null;
    private HttpConnection instanceConnection = null;
    private static String currentInstance;
}