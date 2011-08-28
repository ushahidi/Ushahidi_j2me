package com.ushahidi.j2me.forms;

import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.ushahidi.j2me.App;
import ushahidi.core.I18N;

/**
 * Synchronize Forms
 * @author dalezak
 */
public class Synchronize extends Base {

    public Synchronize(final App app) {
        super(I18N.s("synchronize"));

        Command back = new Command(I18N.s("back"));
        addCommand(back);
        setBackCommand(back);
        addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showDashboard(false);
            }
        });
    }
}
