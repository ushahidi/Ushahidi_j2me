package com.ushahidi.j2me;

import com.sun.lwuit.Display;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import com.ushahidi.j2me.forms.Dashboard;
import com.ushahidi.j2me.forms.Details;
import com.ushahidi.j2me.forms.Reports;
import com.ushahidi.j2me.forms.Settings;
import com.ushahidi.j2me.forms.Splash;
import com.ushahidi.j2me.forms.Synchronize;
import com.ushahidi.j2me.models.Report;
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.midlet.*;

/**
 * @author dalezak
 */
public class Ushahidi extends MIDlet implements App {
    private ushahidi.core.Settings settings;

    public Ushahidi() {
        settings = new ushahidi.core.Settings();
    }

    public void startApp() {
        Display.init(this);
        try {
            Resources resources = Resources.open("/res/Ushahidi.res");
            UIManager.getInstance().setThemeProps(resources.getTheme("Ushahidi"));
         }
         catch(IOException ex) {
             new Alert("UIManager error", ex.getMessage(), null, AlertType.ERROR).setTimeout(50);
         }
        new Splash(this).show();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        //settings.saveDeployment();
        notifyDestroyed();
    }

    public void exit() {
        //settings.saveDeployment();
        notifyDestroyed();
    }

    public void showSplash(boolean forward) {
        Splash splash = new Splash(this);
        splash.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        splash.show();
    }

    public void showDashboard(boolean forward) {
        Dashboard dashboard = new Dashboard(this);
        dashboard.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        dashboard.show();
    }

    public void showReports(boolean forward) {
        Reports reports = new Reports(this);
        reports.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        reports.show();
    }

    public void showDetails(boolean forward, Report report) {
        Details details = new Details(this, report);
        details.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        details.show();
    }

    public void showSettings(boolean forward) {
        Settings settings = new Settings(this);
        settings.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        settings.show();
    }

    public void showSynchronize(boolean forward) {
        Synchronize synchronize = new Synchronize(this);
        synchronize.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, !forward, 500));
        synchronize.show();
    }

    public void showCreate(boolean forward) {
        //new Report(this).show();
    }

    public ushahidi.core.Settings getSettings() {
        return settings;
    }
}
