package ushahidi;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import com.harrison.lee.twitpic4j.TwitPic;
//import com.harrison.lee.twitpic4j.TwitPicResponse;

import com.sun.lwuit.*;

import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.plaf.UIManager;

import com.jappit.midmaps.googlemaps.GoogleMaps;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import ushahidi.core.UshahidiInstance;
import ushahidi.core.UshahidiSettings;
import com.sun.lwuit.util.Resources;
import javax.microedition.midlet.*;
import ushahidi.core.Gmapclass;
import java.io.IOException;
import java.util.Vector;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.media.*;
import javax.microedition.media.control.*;


/**
 * @author toshiba
 */


public class Ushahidi extends MIDlet {
    private Form mainForm,reportForm,viewForm,settingsForm,detailsForm, splashForm,instance; //,mapForm;
    private Button reportButton,viewButton,settingsButton,takephoto,takegallary;
    private TextField  reportsTextField, firstNameTextField, lastNameTextField, emailTextField;
    private Image imglogo;
    private Label logoLabel, mapLabel, lbseparator;
    private TabbedPane tp = null;
    private List incidentsList;
    private ComboBox category,incidentCategory,instanceComboBox;
    private TextField txtitle,txlocation,txdate, instanceName,instanceURL;
    private TextArea txdescri;
    private String[] allcategories = {"Death in kiambu", "Riots in UNOBI", "My pen is stolen"};
    private  String[] items = {"All Categories","Deaths","Riots","Sexual Assalts",
                "Property Loss","Government Forces"};
    private UshahidiSettings settings;
    private UshahidiInstance ushahidiInstance = null;
    private DefaultListModel incidentListModel = null;
    private Form cameraForm;
    private Player player;
    private VideoControl vidControl;
    private MediaComponent mediaComponent;
//    private GoogleMaps gMaps = null;
        
//    GoogleStaticMap map = null;
   
    /**
     * Ushahidi Class constructor<p>
     * Instantiates two objects of the classes UshahidiSettings
     * and UshahidiInstance.
     *
     * @retun void
     */
    public Ushahidi(){
        /**
         * Creates instances of the ushahidi.core.UshahidiSettings.java and
         * ushahidi.core.UshahidiInstance.java classes.
         */
        settings = new UshahidiSettings();
        ushahidiInstance = new UshahidiInstance();
    }

    public void startApp() {
         Display.init(this);

         try {
            Resources res = Resources.open("/res/Ushahidi.res");
            UIManager.getInstance().setThemeProps(res.getTheme("Ushahidi"));
         } catch(IOException ex) {
             Alert uiManAlert = new Alert("UIManager error", ex.getMessage(), null, AlertType.ERROR);
             uiManAlert.setTimeout(50);
         }

         showSplashScreen();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        settings.saveCurrentInstance();
        notifyDestroyed();
    }

    //<editor-fold defaultstate="collapsed" desc=" Main form ">
    public void displayMainForm(){
        mainForm = new Form("Ushahidi");
        mainForm.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/ushahidilogo.png");

            reportButton = new Button("Add Incident");
            viewButton = new Button("View Incidents");
            settingsButton = new Button("Change Settings");

            reportButton.setAlignment(Component.CENTER);
            viewButton.setAlignment(Component.CENTER);
            settingsButton.setAlignment(Component.CENTER);

         // Forms
        lbseparator=new Label("    ");
                    logoLabel = new Label(imglogo);
        logoLabel.setAlignment(Component.CENTER);

        //buttons

        reportButton.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displayReportForm();
          }
        });

        viewButton.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displayViewForm();
          }
        });

        settingsButton.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displaySettingsForm();
          }
        });
        //commands
        //textfields

        //containers
        Container mainMenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container textbox = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        mainMenu.addComponent(logoLabel);
        mainMenu.addComponent(lbseparator=new Label("    "));

        mainMenu.addComponent(reportButton);
        mainMenu.addComponent(lbseparator=new Label("    "));
        mainMenu.addComponent(viewButton);
        mainMenu.addComponent(lbseparator=new Label("    "));
        mainMenu.addComponent(settingsButton);
        mainForm.addComponent(BorderLayout.CENTER, mainMenu);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        Command exitCommand = new Command("Exit");
        mainForm.addCommand(exitCommand);
        mainForm.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                destroyApp(true);
            }
       });

        mainForm.show();
    }
     //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="View incidents ">
    public void displayViewForm() {
        
        Container cate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container mainMenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        final Container eventList = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        //imglogo = Image.createImage("/ushahidi/res/map.png");

//        String [] allcategories = {"Death in kiambu","Riots in UNOBI","A Child is mo...","My pen is stolen","Government is..."};
//        final String [] deaths={"Death in kiambu","Death in Umoja","Death in westlands"};
//        final String [] riots={"Riots in UNOBI","Riots in ANU","Riots in USIU"};
//        final String [] sexual={"A Child is mo...","A Boy is seduces","a woman caught..."};
//            incidentsList = new List();
  
        // Update categories List
        if (getCategoryTitles() != null) {
            items = getCategoryTitles();
            getIncidentFilter(items[0]);
        }

        if (getIncidentTitles().length > 0)
            allcategories = getIncidentTitles();

        incidentListModel = new DefaultListModel(allcategories);
        incidentsList = new List(incidentListModel);
        incidentsList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {                
                int selectedIncidentIndex = incidentListModel.getSelectedIndex();
                getSelectedIncidentByIndex(selectedIncidentIndex);
                displayDetails();
            }
        });

         eventList.addComponent(incidentsList);
         viewForm = new Form("View incidents");
         viewForm.setLayout(new BorderLayout());
         viewForm.setScrollable(false);
         viewForm.setTransitionOutAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_HORIZONTAL, true, 500));

         viewForm.setTransitionInAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_HORIZONTAL, true, 500));

         tp = new TabbedPane();

        mapLabel = new Label(getMap());
        incidentCategory = new ComboBox(items);
        incidentCategory.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
//                ushahidiInstance.getIncidentsByCategoryName((String) incidentCategory.getSelectedItem());
                getIncidentFilter((String) incidentCategory.getSelectedItem());

            if (getIncidentTitles().length > 0)
                allcategories = getIncidentTitles();

                incidentListModel.removeAll();
                
                for ( int i = 0; i < allcategories.length; i++ ) {
                    incidentListModel.addItem(allcategories[i]);
                }
            }
        });
//        mapForm = new Form("Map");
        cate.addComponent(incidentCategory);
        mainMenu.addComponent(mapLabel);
        
        tp.addTab("Reports List", eventList);
        tp.addTab("Reports Map", mainMenu);
        //mainMenu.addComponent(incidentsList);

        viewForm.addComponent(BorderLayout.NORTH, cate);
        viewForm.addComponent("Center", tp);

        viewForm.show();
        viewForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                    displayMainForm();
                }
        });

        viewForm.addCommand(new Command("View") {
            public void actionPerformed(ActionEvent ev) {
                int selectedIncidentIndex = incidentListModel.getSelectedIndex();
                getSelectedIncidentByIndex(selectedIncidentIndex);
                displayDetails();
            }
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Application Settings">
    public void displaySettingsForm(){
         String [] userSetting = settings.getSettings();
         String [] instances = settings.getTitles();

         settingsForm = new Form("Change Settings");
         settingsForm.setLayout(new BorderLayout());

         if (userSetting != null) {
             instanceComboBox = new ComboBox(instances);
             instanceComboBox.setSelectedIndex(Integer.parseInt(userSetting[0]));

             reportsTextField = new TextField(userSetting[1]);
             reportsTextField.setConstraint(TextField.NUMERIC);

             firstNameTextField = new TextField(userSetting[2]);
             lastNameTextField = new TextField(userSetting[3]);
             emailTextField = new TextField(userSetting[4]);
        } else {
             instanceComboBox = new ComboBox(instances);
             reportsTextField = new TextField();
             reportsTextField.setConstraint(TextField.NUMERIC);

             firstNameTextField = new TextField();
             lastNameTextField = new TextField();
             emailTextField = new TextField();
        }
        
         instanceComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                // Save settings
                settings.getInstanceAddressByName(instanceComboBox.getSelectedItem());
                settings.saveSettings(instanceComboBox.getSelectedIndex(), reportsTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText());

                // Prefetch any data that may take long to retrieve
//                if (isConnected())
//                    prefetchMapData();
            }
         });
         
         Container formComponents = new Container(new BoxLayout(BoxLayout.Y_AXIS));
         formComponents.addComponent(new Label("Instance"));
         formComponents.addComponent(instanceComboBox);
         formComponents.addComponent(new Label("No. of reports"));
         formComponents.addComponent(reportsTextField);
         formComponents.addComponent(new Label("First name"));
         formComponents.addComponent(firstNameTextField);
         formComponents.addComponent(new Label("Last name"));
         formComponents.addComponent(lastNameTextField);
         formComponents.addComponent(new Label("E-mail"));
         formComponents.addComponent(emailTextField);

         settingsForm.addComponent(BorderLayout.CENTER, formComponents);

         settingsForm.setTransitionInAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_VERTICAL, true, 500));
         settingsForm.setTransitionOutAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_VERTICAL, true, 500));

         settingsForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                 displayMainForm();
             }
         });

         settingsForm.addCommand(new Command("Save") {
             public void actionPerformed(ActionEvent ev) {
                 //Call function to save settings
                 settings.getInstanceAddressByName(instanceComboBox.getSelectedItem());
                 settings.saveSettings(instanceComboBox.getSelectedIndex(), reportsTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText());

                 // Prefetch any data that may take long to retrieve
                 if(Dialog.show("Load map", "Would you like to preload map data now?", "Yes", "No")) {
//                     if (isConnected())
//                        prefetchMapData();
                 }
             }
         });

         settingsForm.addCommand(new Command("Add instance") {
             public void actionPerformed(ActionEvent ev) {
                addInstance();
             }
         });

         settingsForm.show();


    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Report incident ">
    public void displayReportForm(){
        
        reportForm = new Form("Report incident");
        reportForm.setLayout(new BorderLayout());
        reportForm.setTransitionInAnimator(
        CommonTransitions.createSlide(
        CommonTransitions.SLIDE_HORIZONTAL, true, 500));
        reportForm.setTransitionOutAnimator(
        CommonTransitions.createSlide(
        CommonTransitions.SLIDE_HORIZONTAL, true, 500));
        
        try {            
            logoLabel = new Label(Image.createImage("/ushahidi/res/smallogo.png"));
            logoLabel.setAlignment(Component.CENTER);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        txtitle = new TextField();
        txdescri = new TextArea(3, 20);
        txlocation = new TextField();
        txdate = new TextField(getDate());

        category = new ComboBox(items);

        Container buttonBar = new Container(new BoxLayout(BoxLayout.X_AXIS));
        Container textbox = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        takephoto = (new Button("Take Photo"));
        takephoto.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            showCamera();
              
          }
        });
        takegallary = (new Button("From Gallery"));

        buttonBar.addComponent(takephoto);
        buttonBar.addComponent(takegallary);
        textbox.addComponent(logoLabel);
        textbox.addComponent((new Label("Title")));
        textbox.addComponent(txtitle);
        textbox.addComponent((new Label("Description")));
        textbox.addComponent(txdescri);
        textbox.addComponent((new Label("Location")));
        textbox.addComponent(txlocation);
        textbox.addComponent((new Label("Date")));
        textbox.addComponent(txdate);
        textbox.addComponent((new Label("Categories")));
        textbox.addComponent(category);

        reportForm.addComponent(BorderLayout.CENTER,textbox);
        reportForm.addComponent(BorderLayout.SOUTH, buttonBar);
        reportForm.show();

        // Update the time field every one second
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                txdate.setText(getDate().trim());
                txdate.repaint();
            }
        }, 1000, 1000); // delay, iterate
        
        reportForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                timer.cancel();
                displayMainForm();
            }
        });

        reportForm.addCommand(new Command("Submit") {
            public void actionPerformed(ActionEvent ev) {
                String [] dateField = split(txdate.getText(), " ");
                boolean saved = ushahidiInstance.submitIncident(txtitle.getText(), txdescri.getText(), dateField, txlocation.getText(), category.getSelectedItem().toString());
                if (saved)                    
                    Dialog.show("Succesful", "Your report was succesfully submitted", Dialog.TYPE_CONFIRMATION, null, "Ok", "Cancel");
                else
                    Dialog.show("Failure", "Your report wasn't succesfully submitted", "Ok", "Cancel");
                txtitle.setText("");
                txdescri.setText("");
                txlocation.setText("");
                category.setSelectedIndex(0);
            }
        });

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="display the story">

    public void displayDetails() {
        detailsForm = new Form("Incident Details");
        detailsForm.setLayout(new BorderLayout());

        TextArea incidentDetailsTextArea = new TextArea(getIncidentDetails()[2]);

        if (Ushahidi.getIncidentDetails().length > 0)
            detailsForm.addComponent(BorderLayout.CENTER, incidentDetailsTextArea);

        detailsForm.show();

        detailsForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displayViewForm();
            }
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create an instance">
    public void addInstance() {
        instance = new Form("Add an Instance");
        instance.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/ushahidilogo.png");
            logoLabel = new Label(imglogo);
            logoLabel.setAlignment(Component.CENTER);

            Container mainMenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            
        instanceName = new TextField();
        instanceURL = new TextField("http://");
        instanceURL.setCursorPosition(7);

         mainMenu.addComponent(logoLabel);
         mainMenu.addComponent(new Label("Instance Name"));
         mainMenu.addComponent(instanceName);
           mainMenu.addComponent(new Label("Instance Url"));
         mainMenu.addComponent(instanceURL);

            instance.addComponent(BorderLayout.NORTH, mainMenu);

       } catch (IOException ex) {
            System.err.println(ex.getMessage());
       }

        instance.show();

        instance.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displaySettingsForm();
            }
        });
         instance.addCommand(new Command("Submit") {
            public void actionPerformed(ActionEvent ev) {
                int id = settings.saveInstance(instanceName.getText(), instanceURL.getText());
                if ( id > 0 ) {
                    instanceName.setText("");
                    instanceURL.setText("");
                } //end if
            }
        });

    }
    //</editor-fold>

    /**
     * Performs a data connection test
     *
     * @return true if there is an active data connection
     */
    //<editor-fold defaultstate="collapsed" desc="Connection test">
    private boolean isConnected() {
        boolean connected = false;
        settings.setUshahidiDeployment();
        switch(ushahidiInstance.isConnectionAvailable()) {
            case 200:
                connected = true;
                break;
            case 500:
                if(Dialog.show("Server error", "An internal server error occured.", "Settings", "Exit"))
                    displaySettingsForm();
                else
                    destroyApp(true);
                connected = false;
                break;
        } //end switch

        return connected;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Date">
    private String getDate() {
        String [] dateony=split(new Date().toString()," ");
        return dateony[0]+" "+dateony[1]+" "+dateony[2]+" "+dateony[3]+" "+dateony[5];
    }
    //</editor-fold>
   
    /**
     *Checks if there is a data connection as it displays the
     * Splash screen. Prefetches other data that may take long
     * to load.
     * 
     * @return No value is returned.
     */
   //<editor-fold defaultstate="collapsed" desc="display SplashScreen">
    private void showSplashScreen() {
        splashForm = new Form();
        splashForm.setLayout(new BorderLayout());
        Command exitCommand = new Command("Exit");
        splashForm.addCommand(exitCommand);
        splashForm.setBackCommand(exitCommand);
        splashForm.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                destroyApp(true);
            }
        });

        try {            
            splashForm.getStyle().setBgImage(Image.createImage("/ushahidi/res/splash.jpg"));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        splashForm.show();

        //Performing a connection test
        if(isConnected()) {
            // Fetch Instance data
            prefetchInstanceData("google");

            // Waint until the prefetching is complete before proceeding to
            // the Main Form
            do {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            } while(isPrefetching());
            
            // Once there data is prefetching, open the main form
            splashForm.setTransitionOutAnimator(CommonTransitions.createSlide(
                    CommonTransitions.SLIDE_VERTICAL, false, 300));
            displayMainForm();
            
        }
        else {

        if (Dialog.show("Connection error", "Error establishing data connection. "
            + "\n Please check your phone internet settings\n" +
            " or \n check your credit account.", "Change Instance", "Exit"))
            displayMainForm();
        else
            destroyApp(true);

        }
    }
     //</editor-fold>
  
    private void showCamera() {
        cameraForm = new Form("Capture Image");
        
        try {
            player = Manager.createPlayer("capture://video");
            player.prefetch();
            player.realize();
            mediaComponent = new MediaComponent(player);
           
            vidControl = (VideoControl) mediaComponent.getVideoControl();
            vidControl.setVisible(false);
           
            mediaComponent.setFullScreen(true);
            mediaComponent.start();            
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                    mediaComponent.stop();
                    displayReportForm();

                }
        });
         cameraForm.addCommand(new Command("Capture") {
            public void actionPerformed(ActionEvent ev) {
                captureImage();
            }
        });
       
        cameraForm.addComponent(mediaComponent);
        cameraForm.show();
        
    }

private void captureImage() {
    try {
        
        byte[] raw = vidControl.getSnapshot(null);
        Image image = Image.createImage(raw, 0, raw.length);
        mediaComponent.stop();
        cameraForm.setBgImage(image);
        
    }
    catch (Exception e) {
//showException(e);
    return;

    }
 }

    /**
     * Accepts a String and a separator and returns an array of type String
     *
     * @param string
     * @param separator
     * @return String[]
     */
    //<editor-fold defaultstate="collapsed" desc="string splitter">
    public String[] split(String original, String separator) {
    Vector nodes = new Vector();

    // Parse nodes into vector
    int index = original.indexOf(separator);
    while(index>=0) {
        nodes.addElement( original.substring(0, index) );
        original = original.substring(index+separator.length());
        index = original.indexOf(separator);
    }
    // Get the last node
    nodes.addElement( original );

    // Create splitted string array
    String[] result = new String[ nodes.size() ];
    if( nodes.size()>0 ) {
        for(int loop=0; loop<nodes.size(); loop++)
        result[loop] = (String)nodes.elementAt(loop);
    }
    return result;
}
     //</editor-fold>

    private void getIncidentFilter() {
        Vector incident = ushahidiInstance.getIncidents();
        holdFetchedIncidents(incident); // Hold fetched incidents
        String[] incidentTitles = new String[incident.size()];

        for (int index = 0; index < incident.size(); index++) { // Update index index
            String[] incidentParticulars = (String[]) incident.elementAt(index);
            incidentTitles[index] = incidentParticulars[1]; // Title
        }

        setIncidentTitles(incidentTitles);
    }
    
    private void getIncidentFilter(String categoryName) {
        Vector incident = ushahidiInstance.getIncidentsByCategoryName(categoryName);
//        System.out.println("getIncidentFilter(Line 737): "+ incident.size());
        holdFetchedIncidents(incident); // Hold fetched incidents
        String[] incidentTitles = new String[incident.size()];

        for (int index = 0; index < incident.size(); index++) { // Update index index
            String[] incidentParticulars = (String[]) incident.elementAt(index);
            incidentTitles[index] = incidentParticulars[1]; // Title
        }

        setIncidentTitles(incidentTitles);
    }

    private void getSelectedIncidentByIndex(int index) {
        String[] selectedStory =  (String[]) Ushahidi.fetchedIncidents.elementAt(index);
        setIncidentDetails(selectedStory);
    }
    
    private static void holdFetchedIncidents(Vector fetchedIncidents) {
        Ushahidi.fetchedIncidents = fetchedIncidents;
    }

    private static void setIncidentDetails(String[] incidentDetails) {
        Ushahidi.incidentDetails = incidentDetails;
    }

    private static String[] getIncidentDetails() { return Ushahidi.incidentDetails; }

    private static void setIncidentTitles(String[] incidentTiltles) {
        Ushahidi.reportedIncidentTitles = incidentTiltles;
    }

    private String[] getIncidentTitles() { return reportedIncidentTitles; }

    private String[] getCategoryTitles() { return categoryTitles; }

    private static void setMapApiKey(String mapApiKey) {
        Ushahidi.mapApiKey = mapApiKey;
    }

    public static String getMapApiKey() { return mapApiKey; }
    
    /**@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @ Methods that hold pre-fetched data come here @
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    private void prefetchInstanceData(final String mapSource) throws NullPointerException {
        setPrefetching(true);

        // Pre-fetch data that would otherwise take long to load
        Thread fetchMap = new Thread(new Runnable() {
            String mapKey = null;

            // Handle null exception that occurs when Geographical midpoint
            // is attempted to be retrieved
            public void run() {
                String mapData = ushahidiInstance.getGeographicMidpoint();
                String[] mapDetails = split(mapData, "|");
                double longitude = Double.parseDouble(mapDetails[0].toString());
                double latitude = Double.parseDouble(mapDetails[1].toString());


                if ((mapKey = ushahidiInstance.getApiKey(mapSource)) != null ) {
                    setMapApiKey(mapKey);
                    Gmapclass gMap = new Gmapclass(getMapApiKey());

                    try {                        
                        map = gMap.retrieveStaticImage(320, 240,longitude,latitude, 8, "png32");                        
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                } //end if
            }
        });

        // Thread to fetch Categories in the background
        Thread fetchCategories = new Thread(new Runnable() {

            public void run() {
                categoryTitles = ushahidiInstance.getCategories().getTitles(1);                
            }
        });

        // Thread to fetch Incedences in the background
//        new Thread(new Runnable() {
//
//            public void run() {
//                if (categoryTitles.length != 0)
//                    ushahidiInstance.getIncidentsByCategoryName(categoryTitles[0]);
//            }
//        }).start();
        
        // Synchronize Threads
        fetchMap.start(); // Get Map
        fetchCategories.start(); // Prefetch category info

        do {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } while (fetchMap.isAlive() || fetchCategories.isAlive());
                
        setPrefetching(false);
    }

    private static void setPrefetching(boolean prefetching) {
        Ushahidi.prefetching = prefetching;
    }

    private boolean isPrefetching() { return prefetching; }
    
    private Image getMap() { return map; }

    private static Vector fetchedIncidents = null;
    private static String[] incidentDetails = null;
    private static String[] reportedIncidentTitles = null;
    private static boolean prefetching = false;
    private static String mapApiKey = null;
    private String[] categoryTitles = null;
    private Image map = null;
}