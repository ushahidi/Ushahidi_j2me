package com.ushahidi.j2me.forms;

import com.sun.lwuit.*;

import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.DefaultListModel;

import com.ushahidi.j2me.App;
import com.ushahidi.j2me.models.Database;

import ushahidi.core.I18N;

/**
 * Reports Form
 * @author dalezak
 */
public class Reports extends Base {

    public Reports(final App app) {
        super(I18N.s("Reports"));
        setLayout(new BorderLayout());
        setScrollable(false);
        
        Container container = createdBoxLayout();

        final DefaultListModel reportModel = createModel(Database.getInstance().getReportNames());
        final List reports = createList(reportModel);
        reports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int index = reportModel.getSelectedIndex();
                new Details(app, Database.getInstance().getReport(index)).show();
            }
        });

        final ComboBox categories = createComboBox(Database.getInstance().getCategoryNames());
        categories.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //TODO load reports by category
            }
        });

        container.addComponent(categories);
        container.addComponent(reports);
        addComponent(BorderLayout.NORTH, container);

        Command back = new Command(I18N.s("back"));
        addCommand(back);
        setBackCommand(back);
        addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                app.showDashboard(false);
            }
        });
        
        addCommand(new Command(I18N.s("view")) {
            public void actionPerformed(ActionEvent ev) {
                int index = reportModel.getSelectedIndex();
                app.showDetails(true, Database.getInstance().getReport(index));
            }
        });
    }
}
