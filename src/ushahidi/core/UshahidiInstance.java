/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core;

import java.io.DataOutputStream;
import java.io.InputStream;
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
public class UshahidiInstance {

    /**
     *Tests data connection availability by connecting to http://ushahidi.com/
     * 
     * @return true if a HTTP_CODE 200 is returned and false if any other code is returned
     */
    public boolean isConnectionAvailable() {
        int connectionStatus = 0;

        try {
            String testURL = UshahidiInstance.getUshahidiInstance();
            instanceConnection = (HttpConnection) Connector.open(testURL);
            instanceConnection.setRequestMethod(HttpConnection.HEAD);
            connectionStatus = instanceConnection.getResponseCode();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        } //end finally

        return (connectionStatus == HttpConnection.HTTP_OK)? true: false;
    }

    public static void setUshahidiInstance(String currentInstance) {
        UshahidiInstance.currentInstance = currentInstance;
    }

    public static String getUshahidiInstance() { return currentInstance; }    

    public boolean submitIncident(String incident_title, String incident_description, String[] incident_date, String incident_location, String incident_category) {
        DataOutputStream dataOutputStream = null;
//        InputStream is = null;
        String success = "";

        String ushahidiInstance = UshahidiInstance.getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api") : ushahidiInstance.concat("/api");
        String [] setting = (new UshahidiSettings()).getSettings();        
        String data = "";

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
            "&latitude=-1.28730007",
            "&longitude=36.82145118200820",
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
//            if (is != null) is.close(); // Close InputStream
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {            
            closeHttpConnection();
        } //end finally

        return (success.equals("true"))? true : false;
    }

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

    /**
     * Retries all categories available in an Ushahidi instance
     *
     * @return An XML with the categories in ascending order
     */
    public String getCategories() {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=categories&resp=xml") : ushahidiInstance.concat("/api?task=categories&resp=xml");
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
                           if (categories == null) categories = parser.nextText() + "|";
                           else categories += parser.nextText() + "|";
                        } else if("title".equals(parser.getName())) categories += parser.nextText() + "|";
                        else if("description".equals(parser.getName())) categories += parser.nextText() + "|";
                        else if("color".equals(parser.getName())) categories += parser.nextText() + "~";
            }
                
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }

        return categories;
    }

    public String getCategoryById(int id) {
        // Handles the forward slash at the end of the instance.
        String ushahidiInstance = getUshahidiInstance();
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

    public String getCountries() {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=countries&resp=xml") : ushahidiInstance.concat("/api?task=countries&resp=xml");
        String countries = null;

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("country".equals(parser.getName())) parser.next();
                        if ("id".equals(parser.getName())) {
                            if ( countries == null ) countries = parser.nextText() + "|";
                            else  countries  += parser.nextText() + "|";
                        } else if("iso".equals(parser.getName()))
                            countries  += parser.nextText() + "|";
                        else if("name".equals(parser.getName()))
                            countries  += parser.nextText() + "|";
                        else if("capital".equals(parser.getName()))
                            countries  += parser.nextText() + "~";
                }

            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
        return countries;
    }

    public void getCountryById(int id) {
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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

    public void getIncidents() {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&resp=xml") : ushahidiInstance.concat("/api?task=incidents&resp=xml");

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

    public String getIncidentsByCategoryId(int categoryId) {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by=catid&id="+categoryId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by=catid&id="+categoryId+"&resp=xml");
        String incidents = null;
        
        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            Reader reader = new InputStreamReader(instanceConnection.openInputStream());
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "response");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    if("incident".equals(parser.getName())) parser.next();
                        if("id".equals(parser.getName())) {
                            if (incidents == null) incidents = parser.nextText() + "|";
                            else incidents += parser.nextText() + "|";
                        }
                        else if("title".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("description".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("date".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("mode".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("active".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("verified".equals(parser.getName())) incidents += parser.nextText() + "|";
                        else if("location".equals(parser.getName())) parser.next(); // Location
//                        else if("id".equals(parser.getName())) System.out.println(parser.nextText());
                        else if("name".equals(parser.getName())) incidents += parser.nextText() + "|";
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
                }
            }            
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
        
        return incidents;
    }

    public void getIncidentsByCategoryName(int categoryName) {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by="+categoryName+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by="+categoryName+"&resp=xml");

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

    public void getIncidentsByLocationId(int locationId) {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by="+locationId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by="+locationId+"&resp=xml");

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

    public void getIncidentsByLocationName(int locationName) {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by="+locationName+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by="+locationName+"&resp=xml");

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

    public void getIncidentsBySinceId(int sinceId) {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incidents&by="+sinceId+"&resp=xml") : ushahidiInstance.concat("/api?task=incidents&by="+sinceId+"&resp=xml");

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

    public void getIncidentCount() {
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=incedentcount&resp=xml") : ushahidiInstance.concat("/api?task=incedentcount&resp=xml");

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

    public String getGeographicMidpoint() {
        String ushahidiInstance = getUshahidiInstance();
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
        String ushahidiInstance = getUshahidiInstance();
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

    private String[] trimOutput(Vector vector, int objectSize, int fieldIndex) {
        String[] trimmedOutput = null;
        String[] object = new String[objectSize];
        trimmedOutput = new String[vector.size()];

        for ( int i = 0; i < vector.size(); i++ ) {
            object = (String[]) vector.elementAt(i);
            trimmedOutput[i] = object[fieldIndex];
        }

        return trimmedOutput;
    }

    public String getVersion() {
        String version = null;
        String ushahidiInstance = getUshahidiInstance();
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
        }
    }

    private HttpConnection instanceConnection = null;
    private static String currentInstance;
}
