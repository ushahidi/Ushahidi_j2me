/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ushahidi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author stuart
 */
public class Settings {

    private RecordStore getRecordStore(String recordStore) {
        RecordStore store = null;
        try {
            store = RecordStore.openRecordStore(recordStore, true);
        }
        catch (RecordStoreException e) {
            System.err.println(e.getMessage());
        }
        return store;
    }

    public void setUshahidiDeployment() {
        RecordStore rs = null;
        String deployment = null;

        try {
            rs = getRecordStore("Deployment");
            if (rs.getNumRecords() == 0) {
                deployment = "http://demo.ushahidi.com";
                saveInstance("Demo", "http://demo.ushahidi.com");
            }
            else {
                deployment = this.getDeployment();
            }

        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        finally {
            API.setDeployment(deployment);
        }
    }

    public int saveSettings(int index, String numberOfReports, String firstName, String lastName, String email) {
        RecordStore rs = null;
        int recordID = 0;
        try {
            rs = getRecordStore("SettingsDB");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream writer = new DataOutputStream(byteStream);
            writer.writeUTF(String.valueOf(index)); // Instance address -> Index in ComboBox
            writer.writeUTF(numberOfReports); // Number of reports
            writer.writeUTF(firstName); // First name
            writer.writeUTF(lastName); //Last Name
            writer.writeUTF(email); // E-mail address
            writer.flush();

            byte[] record = byteStream.toByteArray();
            if (rs.getNumRecords() == 0)
                recordID = rs.addRecord(record, 0, record.length);
            else rs.setRecord(1, record, 0, record.length);

            writer.close();
            byteStream.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            closeRecordStore(rs);
        }
        return recordID;
    }

    public String[] getSettings() {
        ByteArrayInputStream inputByteStream;
        DataInputStream reader;
        String[] userSetting = null;
        try {
            RecordStore rs = getRecordStore("SettingsDB");

            try {
                byte[] settings = rs.getRecord(1);
                inputByteStream = new ByteArrayInputStream(settings);
                reader = new DataInputStream(inputByteStream);

                if (rs.getNumRecords()  != 0) {
                    userSetting = new String[5];

                    userSetting[0] = reader.readUTF(); // Instance address
                    userSetting[1] = reader.readUTF(); // Number of reports
                    userSetting[2] = reader.readUTF(); // First name
                    userSetting[3] = reader.readUTF(); //Last Name
                    userSetting[4] = reader.readUTF(); // E-mail address
                } //end if

            }
            catch (InvalidRecordIDException ex) {
                System.err.println(ex.getMessage());
            }
        }
        catch (RecordStoreNotOpenException ex) {
            System.err.println(ex);
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
        return userSetting;
    }

    public int saveInstance(String instanceTitle, String instanceAddress) {
        RecordStore rs = null;
        int recordID = 0;

        try {
            rs = getRecordStore("InstancesDB");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream writer = new DataOutputStream(byteStream);

            writer.writeUTF(instanceTitle); // Instance name
            writer.writeUTF(instanceAddress); // Instance address
            writer.flush();

            byte[] record = byteStream.toByteArray();
            recordID = rs.addRecord(record, 0, record.length);

            writer.close();
            byteStream.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            closeRecordStore(rs);
        }
        
        return recordID;
    }

    public Vector getAllInstances() {
        Vector instances = new Vector();
//        String activeInstance = null;
        RecordStore rs = null;
        ByteArrayInputStream byteStream = null;
        DataInputStream reader = null;

        try {
            rs = getRecordStore("InstancesDB");

                for (int i = 1; i <= rs.getNumRecords(); i++) {
                    byte[] data = rs.getRecord(i);
                    byteStream = new ByteArrayInputStream(data);
                    reader = new DataInputStream(byteStream);

                    String deploymentName = reader.readUTF();
                    String activeInstance = reader.readUTF();

                    instances.addElement(new String[]{deploymentName, activeInstance});

                    // Close streams
                    reader.close();
                    byteStream.close();
                } // end for

        } catch (Exception e) {
            System.err.println(e);
        }

        return instances;
    }

    public String[] getTitles() {
        String[] titles = null;
        Vector instancesVector = getAllInstances();

        titles = new String[instancesVector.size()];

        for (int i = 0; i < instancesVector.size(); i++) {
            String instance[] = (String[]) instancesVector.elementAt(i);
            titles[i] = instance[0];
        }

        return titles;
    }

    public void getDeploymentByName(Object name) {
        String url = null;
        Vector instances = getAllInstances();

        for (int i = 0; i < instances.size(); i++) {
            String[] instance = (String[]) instances.elementAt(i);
            if (instance[0].equals(name)) url = instance[1];
        }

        API.setDeployment(url);
    }

    public int saveDeployment() {
        RecordStore rs = null;
        int recordID = 0;
        try {
            rs = getRecordStore("Deployment");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream writer = new DataOutputStream(byteStream);
            writer.writeUTF(API.getDeployment()); // Address of active Instance
            writer.flush();

            byte[] record = byteStream.toByteArray();

            if (rs.getNumRecords() == 0) {
                recordID = rs.addRecord(record, 0, record.length);
            }
            else {
                rs.setRecord(1, record, 0, record.length);
            }

            writer.close();
            byteStream.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            closeRecordStore(rs);
        }
        return recordID;
    }

    public String getDeployment() {
        String currentInstance = null;
        ByteArrayInputStream byteStream = null;
        DataInputStream reader = null;
        RecordStore rs = null;

        try {
            rs = getRecordStore("Deployment");

            byte[] data = rs.getRecord(1);
            byteStream = new ByteArrayInputStream(data);
            reader = new DataInputStream(byteStream);
            currentInstance = reader.readUTF().toString();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            closeRecordStore(rs);
        }

        return currentInstance;
    }

    private void closeRecordStore(RecordStore recordStore) {
        if (recordStore != null) {
            try {
                recordStore.closeRecordStore();
            }
            catch (RecordStoreException e) {
                System.err.println(e.getMessage());
            }
        } //end if
    }
}