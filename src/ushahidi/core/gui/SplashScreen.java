/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ushahidi.core.gui;

//import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ushahidi.Ushahidi;

/**
 *
 * @author stuart
 */
public class SplashScreen extends Canvas implements Runnable {

    public SplashScreen(Ushahidi ushahidiMidlet) {
        this.ushahidiMidlet = ushahidiMidlet;
        startSplashScreen();
    }
    
    private void startSplashScreen() {

        try {
            ushahidiSplash = Image.createImage("/ushahidi/res/splash.jpg");
            Thread splash = new Thread(this);
            splash.start();
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    private void setProgress(int progress) {
        Gauge progressGauge = new Gauge(null, false, 200, 1);
        progressGauge.setValue(2);
    }

    protected void paint(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        g.drawImage(ushahidiSplash, width, height, Graphics.LEFT | Graphics.TOP);
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private Ushahidi ushahidiMidlet;
    private Image ushahidiSplash;
}
