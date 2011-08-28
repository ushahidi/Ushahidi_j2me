package com.ushahidi.j2me.forms;

import com.sun.lwuit.*;

import com.sun.lwuit.layouts.BorderLayout;
import com.ushahidi.j2me.App;
import com.sun.lwuit.events.ActionEvent;

import com.sun.lwuit.events.ActionListener;
import ushahidi.core.I18N;

/**
 * Details Form
 * @author dalezak
 */
public class Details extends Base {

    public Details(final App app, com.ushahidi.j2me.models.Report report) {
        super(I18N.s("details"));
        
        Container container = createdBoxLayout();

        container.addComponent(createLabel(I18N.s("date")));
        container.addComponent(createTextField(report.getDateString(), false));

        container.addComponent(createLabel(I18N.s("title")));
        container.addComponent(createTextField(report.getTitle(), false));

        container.addComponent(createLabel(I18N.s("description")));
        container.addComponent(createTextArea(report.getDescription(), false));

        container.addComponent(createLabel(I18N.s("location")));
        container.addComponent(createTextField(report.getLocation(), false));
        container.addComponent(createTextField(report.getCoordinates(), false));

        container.addComponent(createLabel(I18N.s("map")));
        container.addComponent(createImageLabel(report.getMap()));

        container.addComponent(createLabel(I18N.s("photos")));
        for (int i=0; i<report.getPhotoCount();i++) {
            container.addComponent(createImageLabel(report.getPhoto(i)));
        }

        addComponent(BorderLayout.NORTH, container);

        Command back = new Command(I18N.s("back"));
        addCommand(back);
        setBackCommand(back);
        addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                app.showReports(false);
            }
        });
    }
}
