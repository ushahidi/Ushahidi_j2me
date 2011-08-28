package com.ushahidi.j2me.forms;

import com.sun.lwuit.*;

import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

import com.ushahidi.j2me.App;

import java.util.Enumeration;
import javax.microedition.io.file.FileSystemRegistry;
import org.json.me.JSONException;
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

    public void onShowCompleted() {
        super.onShowCompleted();
        getRoots();
        com.ushahidi.j2me.models.Report report = new com.ushahidi.j2me.models.Report();
        report.setID(1);
        report.setTitle("Title 1");
        report.setDescription("Description 1");
        report.setLatitude("Latitude 1");
        report.setLongitude("Longitude 1");
        System.out.println("JSON: " + report.toString());
        if (report.save()) {
            System.out.println("Save Succeeded!");
        } else {
            System.out.println("Save Failed!");
        }
        com.ushahidi.j2me.models.Report report2 = com.ushahidi.j2me.models.Report.load("file:///root1/1.txt");
        System.out.println("Loaded " + report2.toString());

    }

    private void getRoots() {
      Enumeration drives = FileSystemRegistry.listRoots();
      System.out.println("The valid roots found are: ");
      while(drives.hasMoreElements()) {
         String root = (String) drives.nextElement();
         System.out.println("\t"+root);
      }
   }
}
