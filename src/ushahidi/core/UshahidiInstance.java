/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core;

import java.io.DataInputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

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
            instanceConnection.setRequestMethod(HttpConnection.GET);
            connectionStatus = instanceConnection.getResponseCode();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            closeHttpConnection();
        } //end finally

        return (connectionStatus == HttpConnection.HTTP_OK)? true: false;
    }

    public void setUshahidiInstance(String currentInstance) {
        this.currentInstance = currentInstance;
    }

    public String getUshahidiInstance() { return currentInstance; }

    /**
     * Retries all categories available in an Ushahidi instance
     *
     * @return An xml with the categories in ascending order
     */
    public void getCategories() {
        // Handles the forward slash at the end of the instance.
        String ushahidiInstance = getUshahidiInstance();
        String url = (ushahidiInstance.endsWith("/"))? ushahidiInstance.concat("api?task=categories&resp=xml") : ushahidiInstance.concat("/api?task=categories&resp=xml");

        try {
            instanceConnection = (HttpConnection) Connector.open(url);
            instanceConnection.setRequestMethod(HttpConnection.GET);

            if(instanceConnection.getResponseCode() == HttpConnection.HTTP_OK) {
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

    private HttpConnection instanceConnection = null;
    private DataInputStream dataInputStream;
    private String currentInstance;
    private String message;
}
