package com.ushahidi.j2me.forms;

import com.sun.lwuit.*;

import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;

import com.ushahidi.j2me.App;
import ushahidi.core.I18N;

/**
 * Settings Form
 * @author dalezak
 */
public class Settings extends Base {

    private ushahidi.core.Settings settings = new ushahidi.core.Settings();

    public Settings(final App app) {
        super(I18N.s("settings"));
        setLayout(new BorderLayout());
        
        String[] userSetting = settings.getSettings();
        String[] titles = settings.getTitles();

        Container container = createdBoxLayout();

        final ComboBox languages = createComboBox();
        final TextField reports = createTextField();
        final ComboBox deployments = createComboBox(titles);
        final TextField firstName = createTextField();
        final TextField lastName = createTextField();
        final TextField email = createTextField();

        if (userSetting != null) {
            deployments.setSelectedIndex(Integer.parseInt(userSetting[0]));
            reports.setText(userSetting[1]);
            firstName.setText(userSetting[2]);
            lastName.setText(userSetting[3]);
            email.setText(userSetting[4]);
        }
        //LANGUAGE
        container.addComponent(createLabel(I18N.s("language")));
        languages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //settings.getDeploymentByName(deployments.getSelectedItem());
                //settings.saveSettings(deployments.getSelectedIndex(), reports.getText(), firstName.getText(), lastName.getText(), email.getText());
            }
        });
        container.addComponent(languages);

        //DEPLOYMENT
        container.addComponent(createLabel(I18N.s("deployment")));
        deployments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                settings.getDeploymentByName(deployments.getSelectedItem());
                settings.saveSettings(deployments.getSelectedIndex(), reports.getText(), firstName.getText(), lastName.getText(), email.getText());
            }
        });
        container.addComponent(deployments);

        //NUMBER OF REPORTS
        container.addComponent(createLabel(I18N.s("no_of_reports")));
        container.addComponent(reports);

        //FIRST NAME
        container.addComponent(createLabel(I18N.s("first_name")));
        container.addComponent(firstName);

        //LAST NAME
        container.addComponent(createLabel(I18N.s("last_name")));
        container.addComponent(lastName);

        //EMAIL
        container.addComponent(createLabel(I18N.s("email")));
        if (userSetting != null) {
            email.setText(userSetting[4]);
        }
        container.addComponent(email);

        addComponent(BorderLayout.CENTER, container);

        addCommand(new Command(I18N.s("back")) {
            public void actionPerformed(ActionEvent ev) {
                app.showDashboard(false);
            }
        });
        addCommand(new Command(I18N.s("save")) {
            public void actionPerformed(ActionEvent ev) {
                settings.getDeploymentByName(deployments.getSelectedItem());
                settings.saveSettings(deployments.getSelectedIndex(), reports.getText(), firstName.getText(), lastName.getText(), email.getText());
                if (Dialog.show(I18N.s("restart"), "A restart is needed to load selected instance. Would you wish to exit the application now?", I18N.s("yes"), I18N.s("no"))) {
                    app.exit();
                }
                else {
                    app.showDashboard(false);
                }
            }
        });
        addCommand(new Command(I18N.s("add_deployment")) {
            public void actionPerformed(ActionEvent ev) {
                //app.showCreate(true);
            }
        });

    }
}
