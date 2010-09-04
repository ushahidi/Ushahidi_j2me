/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core;

import java.io.InputStreamReader;
import java.io.Reader;
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
            instanceConnection = (HttpConnection) Connector.open("http://www.ushahidi.com");
            instanceConnection.setRequestMethod(HttpConnection.HEAD);
            connectionStatus = instanceConnection.getResponseCode();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            closeHttpConnection();
        } //end finally

        return (connectionStatus == HttpConnection.HTTP_OK)? true: false;
    }

    public void setUshahidiInstance(String currentInstance) {
        UshahidiInstance.currentInstance = currentInstance;
    }

    public static String getUshahidiInstance() { return currentInstance; }

    /**
     * Retries all categories available in an Ushahidi instance
     *
     * @return An XML with the categories in ascending order
     */
    public void getCategories() {
        // Handles the forward slash at the end of the instance.
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=categories&resp=xml") : ushahidiInstance.concat("/api?task=categories&resp=xml");

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
            System.out.println(e.getMessage());
        } finally {
            closeHttpConnection();
        }
    }

    private void closeHttpConnection() {
        try {
            if (instanceConnection != null) instanceConnection.close();
        } catch (Exception e) {
            //Exception handler here
        }
    }

    class Category {

        public Category(int id, String title, String description, String color) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.color = color;
        }

        public void setID(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setColor(String color) {
            this.color = color;
        }
        public int getID() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getColor() { return color; }

        private int id;
        private String title;
        private String description;
        private String color;
    }

    private HttpConnection instanceConnection = null;
    private static String currentInstance;
}
