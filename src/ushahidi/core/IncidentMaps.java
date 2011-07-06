package ushahidi.core;

import com.sun.lwuit.Image;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class IncidentMaps {
    private static final String URL_UNRESERVED =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "abcdefghijklmnopqrstuvwxyz" +
        "0123456789-_.~";
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    // these 2 properties will be used with map scrolling methods. You can remove them if not needed
    public static final int offset = 268435456;
    public static final double radius = offset / Math.PI;

    private static String apiKey = null;

    public IncidentMaps(String key) {
        apiKey = key;
    }

    public IncidentMaps() {}
    
    public static void setMapAPIKey(String apiKey) {
        IncidentMaps.apiKey = apiKey;
    }
    
    public double[] geocodeAddress(String address) throws Exception {
        byte[] res = loadHttpFile(getGeocodeUrl(address));
        String[] data = split(new String(res), ',');

        if (!data[0].equals("200")) {
            int errorCode = Integer.parseInt(data[0]);
            throw new Exception("Google Maps Exception: " + getGeocodeError(errorCode));
        }

        return new double[] {
                Double.parseDouble(data[2]), Double.parseDouble(data[3])
        };
    }

    public Image retrieveIncidentMap(int width, int height, double lat, double lng, int zoom) throws IOException {
        byte[] imageData = loadHttpFile(getIncidentMapUrl(width, height, lng, lat, zoom));
        return Image.createImage(imageData, 0, imageData.length);
    }
    
    public Image retrieveStaticImage(int width, int height, double lat, double lng, int zoom,
            String format) throws IOException {
        byte[] imageData = loadHttpFile(getMapUrl(width, height, lng, lat, zoom, format));

        return Image.createImage(imageData, 0, imageData.length);
    }

    private static String getGeocodeError(int errorCode) {
        switch (errorCode) {
        case 400:
            return "Bad request";
        case 500:
            return "Server error";
        case 601:
            return "Missing query";
        case 602:
            return "Unknown address";
        case 603:
            return "Unavailable address";
        case 604:
            return "Unknown directions";
        case 610:
            return "Bad API key";
        case 620:
            return "Too many queries";
        default:
            return "Generic error";
        }
    }

    private String getGeocodeUrl(String address) {
        return "http://maps.google.com/maps/geo?q=" + urlEncode(address) + "&output=csv&key="
                + apiKey;
    }

    private String getIncidentMapUrl(int width, int height, double lng, double lat, int zoom) {
        return "http://maps.google.com/maps/api/staticmap?center="+lat+","+lng+"&zoom="+zoom+"&size="
                +width+"x"+height+"&maptype=roadmap&markers=color:red|label:A|"+lat+","+lng
                +"&sensor=false";                
    }
    
    private String getMapUrl(int width, int height, double lng, double lat, int zoom, String format) {
        return "http://maps.google.com/staticmap?center=" + lat + "," + lng + "&format="
                + format + "&zoom=" + zoom + "&size=" + width + "x" + height + "&key=" + apiKey;
    }

    private static String urlEncode(String str) {
        StringBuffer buf = new StringBuffer();
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(str);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        for (int i = 2; i < bytes.length; i++) {
            byte b = bytes[i];
            if (URL_UNRESERVED.indexOf(b) >= 0) {
                buf.append((char) b);
            } else {
                buf.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
            }
        }
        return buf.toString();
    }

    private static byte[] loadHttpFile(String url) throws IOException {
        byte[] byteBuffer;

        HttpConnection hc = (HttpConnection) Connector.open(url);
        try {
            hc.setRequestMethod(HttpConnection.GET);
            InputStream is = hc.openInputStream();
            try {
                int len = (int) hc.getLength();
                if (len > 0) {
                    byteBuffer = new byte[len];
                    int done = 0;
                    while (done < len) {
                        done += is.read(byteBuffer, done, len - done);
                    }
                } else {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[512];
                    int count;
                    while ( (count = is.read(buffer)) >= 0 ) {
                        bos.write(buffer, 0, count);
                    }
                    byteBuffer = bos.toByteArray();
                }
            } finally {
                is.close();
            }
        } finally {
            hc.close();
        }

        return byteBuffer;
    }

    private static String[] split(String s, int chr) {
        Vector res = new Vector();

        int curr;
        int prev = 0;

        while ( (curr = s.indexOf(chr, prev)) >= 0 ) {
            res.addElement(s.substring(prev, curr));
            prev = curr + 1;
        }
        res.addElement(s.substring(prev));

        String[] splitted = new String[res.size()];
        res.copyInto(splitted);

        return splitted;
    }
}