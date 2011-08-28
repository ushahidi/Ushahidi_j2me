package com.ushahidi.j2me.models;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

/**
 * Base Model
 * @author dalezak
 */
public abstract class Model extends JSONObject {

    //public abstract boolean load();

    public abstract boolean save();

    protected Model() {
        
    }
    protected Model(String json) throws JSONException {
        super(json);
    }

    protected boolean save(String path) {
        try {
            System.out.println("save " + path);
            FileConnection connection = (FileConnection) Connector.open(path, Connector.WRITE);
            OutputStream out = connection.openOutputStream(connection.fileSize());
            PrintStream output = new PrintStream(out);
            output.println(toString());
            out.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    protected static String fromString(String path) {
        try {
            FileConnection connection = (FileConnection) Connector.open(path, Connector.READ);
            InputStream in = connection.openInputStream();
            byte b[] = new byte[1024];
            int length = in.read(b, 0, 1024);
            return new String(b, 0, length);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected Vector getRoots() {
        Enumeration drives = FileSystemRegistry.listRoots();
        Vector roots = new Vector();
        while (drives.hasMoreElements()) {
            String root = (String) drives.nextElement();
            System.out.println("Root: " + root);
            roots.addElement(root);
        }
        return roots;
    }

    protected String getDefaultRoot() {
        return getRoots().size() > 0 ? getRoots().elementAt(0).toString() : "";
    }
    
}
