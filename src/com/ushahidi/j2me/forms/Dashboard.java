package com.ushahidi.j2me.forms;

import com.sun.lwuit.*;

import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

import com.ushahidi.j2me.App;

import ushahidi.core.I18N;

/**
 * Dashboard Form
 * @author dalezak
 */
public class Dashboard extends Base {

    public Dashboard(final App app) {
        super(I18N.s("ushahidi"));
        setLayout(new BorderLayout());

        //CONTAINER
        Container container = createdBoxLayout();
        //LOGO
        container.addComponent(createImageLabel("/ushahidi/res/logo.png"));
        //ADD REPORT
        container.addComponent(createButton(I18N.s("add_report"), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showCreate(true);
            }
        }));
        //VIEW REPORTS
        container.addComponent(createButton(I18N.s("view_reports"), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showReports(true);
            }
        }));
        //SYNCHRONIZE
        container.addComponent(createButton(I18N.s("synchronize"), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showSynchronize(true);
            }
        }));
        //SETTINGS
        container.addComponent(createButton(I18N.s("settings"), new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showSettings(true);
            }
        }));
        addComponent(BorderLayout.CENTER, container);

        Command exitCommand = new Command(I18N.s("exit"));
        addCommand(exitCommand);
        addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.exit();
            }
        });
    }
}
