/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author stuart
 */
public class UshahidiInstance {

    public String getInstance(String url) {
        try {
            instance = (HttpConnection) Connector.open(url);
            instance.setRequestMethod(HttpConnection.GET);
            int responseCode = instance.getResponseCode();
            
            System.out.println(responseCode);
            if(responseCode == instance.HTTP_OK) {
                dataInputStream = new DataInputStream(instance.openDataInputStream());
//                Read from web server character by charcter
                int ch;
                while((ch = dataInputStream.read()) != -1) {
                    message+=(char)ch;
                }
            }

        } catch (IOException ex) {
            System.err.println(ex);
            message = "ERROR";
        } finally {
            try { if (instance != null) instance.close(); } catch(IOException e) {}
            try { if (dataInputStream != null) dataInputStream.close(); } catch(IOException e) {}
        }

        return message;
    }

    public void createInstance(String url) {
    }

    public void listInstances() {
    }

    private DataInputStream dataInputStream;
    private HttpConnection instance;
    private String message;
}
