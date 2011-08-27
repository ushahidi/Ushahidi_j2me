package ushahidi;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.lwuit.*;

import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.plaf.UIManager;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import ushahidi.core.API;
import ushahidi.core.Settings;
import com.sun.lwuit.util.Resources;
import javax.microedition.midlet.*;
import ushahidi.core.Maps;
import java.io.IOException;
import java.util.Vector;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import ushahidi.core.I18N;


/**
 * @author toshiba
 */


public class Ushahidi extends MIDlet  {
    private Form mainForm,reportForm,viewForm,settingsForm,detailsForm, splashForm,instance;    
    private Button reportButton,viewButton,settingsButton,takephoto,takegallery;
    private TextField  reportsTextField, firstNameTextField, lastNameTextField, emailTextField;
    private Image imglogo;
    private Label logoLabel, lbseparator;
    private List incidentsList;
    private ComboBox reportCategoriesComboBox,categoriesComboBox,instanceComboBox;
    private TextField txtitle,txlocation,txdate, instanceName,instanceURL;
    private TextArea txdescri;
    private String[] categoryIncidentTitles = {};
    private  String[] categoryNames = {};
    private Settings settings;
    private API api = null;
    private DefaultListModel incidentListModel = null;
    private Form cameraForm;
    private Player player;
    private VideoControl vidControl;
    private MediaComponent mediaComponent;
           
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
        settings = new Settings();
        api = new API();
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
        settings.saveDeployment();
        notifyDestroyed();
    }

    //<editor-fold defaultstate="collapsed" desc=" Main form ">
    public void displayMainForm(){
        mainForm = new Form(I18N.s("ushahidi"));
        

        try {
            mainForm.setLayout(new BorderLayout());
            imglogo = Image.createImage("/ushahidi/res/logo.png");

            reportButton = new Button(I18N.s("add_report"));
            viewButton = new Button(I18N.s("view_reports"));
            settingsButton = new Button(I18N.s("settings"));

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
        Container container = new Container(new BoxLayout(BoxLayout.Y_AXIS));        
        
        // Update categories List
        if (getCategoryTitles() != null && (getCurrentCategoryIndex() == -1)) {    
            getIncidentFilter(getCategoryTitles()[0]);
        } else {
            getIncidentFilter(getCategoryTitles()[getCurrentCategoryIndex()]);
        }
        
        // Get list of incidents under the first reportCategoriesComboBox
        if (getIncidentTitles().length > 0)
            categoryIncidentTitles = getIncidentTitles();
        
        incidentListModel = new DefaultListModel(getIncidentTitles());
        incidentsList = new List(incidentListModel);
        incidentsList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                int selectedIncidentIndex = incidentListModel.getSelectedIndex();
                getSelectedIncidentByIndex(selectedIncidentIndex);
                displayDetails();
            }
        });

         viewForm = new Form("View incidents");
         viewForm.setLayout(new BorderLayout());
         viewForm.setScrollable(false);
         viewForm.setTransitionOutAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_HORIZONTAL, true, 500));

         viewForm.setTransitionInAnimator(
         CommonTransitions.createSlide(
         CommonTransitions.SLIDE_HORIZONTAL, true, 500));

        categoriesComboBox = new ComboBox(categoryNames);        
        if (getCurrentCategoryIndex() != -1) 
            categoriesComboBox.setSelectedIndex(getCurrentCategoryIndex());
        
        categoriesComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) { 
                setCurrentCategoryIndex(categoriesComboBox.getSelectedIndex());
                getIncidentFilter((String) categoriesComboBox.getSelectedItem());
                
                if (getIncidentTitles().length > 0)
                    categoryIncidentTitles = getIncidentTitles();

                    incidentListModel.removeAll();

                    for ( int i = 0; i < categoryIncidentTitles.length; i++ ) {
                        incidentListModel.addItem(categoryIncidentTitles[i]);
                    }
                }
        });       
        

        container.addComponent(categoriesComboBox);
        container.addComponent(incidentsList);

        viewForm.addComponent(BorderLayout.NORTH, container);
        viewForm.show();

        viewForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                    displayMainForm();
                }
        });
        
        viewForm.addCommand(new Command("View") {
            public void actionPerformed(ActionEvent ev) {
                int selectedIncidentIndex = incidentListModel.getSelectedIndex();
                if (selectedIncidentIndex > -1 && Ushahidi.fetchedIncidents.size() > selectedIncidentIndex) {
                    getSelectedIncidentByIndex(selectedIncidentIndex);
                    displayDetails();
                }
            }
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Application Settings">
    public void displaySettingsForm(){
         String [] userSetting = settings.getSettings();
         String [] instances = settings.getTitles();

         settingsForm = new Form("Settings");
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
                settings.getDeploymentByName(instanceComboBox.getSelectedItem());
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
                 settings.getDeploymentByName(instanceComboBox.getSelectedItem());
                 settings.saveSettings(instanceComboBox.getSelectedIndex(), reportsTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText());

                 // Prefetch any data that may take long to retrieve
                 if(Dialog.show("Restart", "A restart is needed to load selected"
                         + " instance. Would you wish to exit the application now?", "Yes", "No"))
                     destroyApp(true);
                 else
                     displayMainForm();
             }
         });

         settingsForm.addCommand(new Command("Add instance") {
             public void actionPerformed(ActionEvent ev) {
                addUshahidiInstance();
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
            logoLabel = new Label(Image.createImage("/ushahidi/res/logo-small.png"));
            logoLabel.setAlignment(Component.CENTER);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        txtitle = new TextField();
        txdescri = new TextArea(3, 20);
        txlocation = new TextField();
        txdate = new TextField(getDate());

        reportCategoriesComboBox = new ComboBox(getCategoryTitles());

        Container buttonBar = new Container(new BoxLayout(BoxLayout.X_AXIS));
        Container textbox = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        takephoto = (new Button("Take Photo"));
        takephoto.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            showCamera();              
          }
        });
        takegallery = (new Button("From Gallery"));

        //buttonBar.addComponent(takephoto);
        //buttonBar.addComponent(takegallery);
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
        textbox.addComponent(reportCategoriesComboBox);

        reportForm.addComponent(BorderLayout.CENTER,textbox);
        reportForm.addComponent(BorderLayout.SOUTH, buttonBar);
        reportForm.show();

        // Update the time field every one second
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                try{
                txdate.setText(getDate().trim());
                txdate.repaint();
                }catch(Exception ex){
                    System.err.println(ex.getMessage());
                }
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
                boolean saved = api.submitIncident(txtitle.getText(), txdescri.getText(), dateField, txlocation.getText(), reportCategoriesComboBox.getSelectedItem().toString());
                System.out.println("Application saved? "+saved);
                if (saved)
                    Dialog.show("Succesful", "Your report was succesfully submitted", Dialog.TYPE_CONFIRMATION, null, "Ok", "Cancel");
                else
                    Dialog.show("Failure", "An error occured while submitting your report.", "Ok", "Cancel");
                txtitle.setText("");
                txdescri.setText("");
                txlocation.setText("");
                reportCategoriesComboBox.setSelectedIndex(0);
            }
        });

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Incident Details">
    public void displayDetails() {
        detailsForm = new Form("Incident Details");
        detailsForm.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        String[] incidentParticulars = getIncidentDetails();
        String incidentTitle = incidentParticulars[1];
        String incidentDescription = incidentParticulars[2];
        String incidentDate = incidentParticulars[3];
        String locationName = incidentParticulars[4];
        String latitude = incidentParticulars[5];        
        String longitude = incidentParticulars[6];
        
        // Get Map of the incident location
        Image mapImg = null;
        try {
            mapImg = new Maps(ushahidi.core.Maps.getMapAPIKey())
                    .retrieveIncidentMap(320, 240, Double.parseDouble(latitude), Double.parseDouble(longitude), 8);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        Label incidentMapLabel = new Label(mapImg);
        TextField dateTextField = new TextField(incidentDate);
        dateTextField.setEditable(false);
        
        TextField titleTextField = new TextField(incidentTitle);
        titleTextField.setEditable(false);
        
        TextField locNameTextField = new TextField(locationName);
        locNameTextField.setEditable(false);
        
        if (Ushahidi.getIncidentDetails().length > 0) {
            detailsForm.addComponent(new Label("Date"));
            detailsForm.addComponent(dateTextField);
            detailsForm.addComponent(new Label("Title"));
            detailsForm.addComponent(titleTextField);
            detailsForm.addComponent(new Label("Location name"));
            detailsForm.addComponent(locNameTextField);
            detailsForm.addComponent(new Label("Description"));       
            detailsForm.addComponent(new TextArea(incidentDescription));
            detailsForm.addComponent(new Label("Map"));
            detailsForm.addComponent(incidentMapLabel);
        }

        detailsForm.show();

        detailsForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displayViewForm();
            }
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create an instance">
    public void addUshahidiInstance() {
        instance = new Form("Add an Instance");
        instance.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/logo.png");
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
        switch(api.isConnectionAvailable()) {
            case 200:
                connected = true;
                break;
            case 500:
                if(Dialog.show("Server error", "An internal server error occured.", "Settings", "Exit")){
                    displaySettingsForm();
                }
                else{
                    destroyApp(true);
                connected = false;
                }
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
                    Thread.sleep(3500);
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                    
                }
            } while(isPrefetching());
            
            // Once there data is prefetching, open the main form
            splashForm.setTransitionOutAnimator(CommonTransitions.createSlide(
                    CommonTransitions.SLIDE_VERTICAL, false, 300));
            displayMainForm();
            
        }
        else {

        if (Dialog.show("Connection error", "There was an error establishing data connection."
            + "\nPlease check your phone internet settings or your credit balance." , "Settings" , "Exit"))
            displaySettingsForm();
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
            System.err.println(e.getMessage());
        }
        cameraForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                    mediaComponent.stop();
                    
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

       
        // Create TwitPic object and allocate TwitPicResponse object
//        TwitPic tpRequest = new TwitPic("ushahidij2me", "ushahidilwuit");
//        TwitPicResponse tpResponse = null;
//
//        // Make request and handle exceptions
//        try {
//            tpResponse = tpRequest.uploadAndPost(raw, "Incident Photo");
//        } catch (IOException e) {
//            //e.printStackTrace();
//            System.err.println("error");
//        } catch (TwitPicException e) {
//          // e.printStackTrace();
//           System.err.println("error");
//        }
//
//        // If we got a response back, print out response variables
//        if(tpResponse != null)
//            tpResponse.dumpVars();

    }catch (Exception e) {
        System.err.println(e.getMessage());
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

//    private void getIncidentFilter() {
//        Vector incident = api.getIncidents();
//        holdFetchedIncidents(incident); // Hold fetched incidents
//        String[] incidentTitles = new String[incident.size()];
//
//        for (int index = 0; index < incident.size(); index++) { // Update index index
//            String[] incidentParticulars = (String[]) incident.elementAt(index);
//            incidentTitles[index] = incidentParticulars[1]; // Title
//        }
//
//        setIncidentTitles(incidentTitles);
//    }
    
    private void getIncidentFilter(String categoryName) {
        Vector incident = api.getIncidentsByCategoryName(categoryName);
        holdFetchedIncidents(incident); // Hold fetched incidents
        String[] incidentTitles = new String[incident.size()];

        for (int index = incident.size() - 1; index >= 0; --index) { // Update index index
            String[] incidentParticulars = (String[]) incident.elementAt(index);
            incidentTitles[index] = incidentParticulars[1]; // Title
        }

        setIncidentTitles(incidentTitles);
    }

    private void setCurrentCategoryIndex(int currentCategoryIndex) { 
        Ushahidi.currentCategoryIndex = currentCategoryIndex; 
    }

    private int getCurrentCategoryIndex() { return currentCategoryIndex; }
    
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

    private static void setIncidentTitles(String[] incidentTitles) {
        Ushahidi.reportedIncidentTitles = incidentTitles;
    }

    private String[] getIncidentTitles() { return reportedIncidentTitles; }

    private String[] getCategoryTitles() { return categoryTitles; }
    
    /**@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @ Methods that hold pre-fetched data come here @
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    private void prefetchInstanceData(final String mapSource) throws NullPointerException {
        setPrefetching(true);

        
        // Load Map API Keys from the Instance
        new Thread(new Runnable() {
            
            public void run() {
                api.getAPIKey("google");
            }            
        }).start();        

        // Thread to fetch Categories in the background
        Thread fetchCategories = new Thread(new Runnable() {

            public void run() {
                try{
                categoryTitles = api.getCategories().getTitles(1);
                }catch(Exception ex){
                 System.err.println(ex.getMessage());
                }
            }
        });

        // Thread to fetch IncidencesSinceId in the background
        Thread fetchRecentIncidents = new Thread(new Runnable() {
            
            public void run() {
            }
        });
        fetchCategories.start(); // Prefetch reportCategoriesComboBox info

        do {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } while (fetchCategories.isAlive()); //       } while (fetchMap.isAlive() || fetchCategories.isAlive());
                
        setPrefetching(false);
    }

    private static void setPrefetching(boolean prefetching) {
        Ushahidi.prefetching = prefetching;
    }

    private boolean isPrefetching() { return prefetching; }
    
    private static int currentCategoryIndex = -1;
    
    private static Vector fetchedIncidents = null;
    private static String[] incidentDetails = null;
    private static String[] reportedIncidentTitles = null;
    private static boolean prefetching = false;
    private static String[] categoryTitles = null;
}