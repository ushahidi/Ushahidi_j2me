package com.ushahidi.j2me.forms;

import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.ushahidi.j2me.App;

import java.util.Timer;
import java.util.TimerTask;
import ushahidi.core.I18N;

/**
 *
 * @author dalezak
 */
public class Splash extends Base {

    private App app;
    public Splash(final App app) {
        super(null);
        this.app = app;
        setLayout(new BorderLayout());

        getStyle().setBgImage(createImage("/ushahidi/res/splash.jpg"));

        Command exit = new Command(I18N.s("exit"));
        addCommand(exit);
        setBackCommand(exit);
        addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.exit();
            }
        });
    }

    public void onShowCompleted() {
        super.onShowCompleted();
        new Timer().schedule(new LoadTimerTask(), 700);
    }

    private class LoadTimerTask extends TimerTask {
        public final void run(){
            app.showDashboard(true);
        }
    }
}
