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
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import java.util.Calendar;

//import javax.microedition.lcdui.CommandListener;
//import javax.microedition.lcdui.DateField;
import java.util.Date;
import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.midlet.*;
import ushahidi.core.UshahidiInstance;


/**
 * @author toshiba
 */

public class Ushahidi extends MIDlet {
    private Form mainForm,reportForm,viewForm,settingsForm,detailsForm, splashForm,instance;
    private Button btreport,btview,btsettings,takephoto,takegallary;
    private Label lbtitle,lbdescri,lblocation,lbdate,lbcatego;
    private TextField  reportsTextField, firstNameTextField, lastNameTextField, emailTextField;
    private Image imglogo;
    private Label lblogo,lblist1,lblist2,lblist3,lblist4,lblist5,lbmap,lbseparator;
    private TabbedPane tp = null;
    private List eventlists;
    private ComboBox category,mapcategory,instanceTextField;
    private TextField txtitle,txlocation,txdate,txcatego,instancename,instanceurl;
    private TextArea txdescri;
    private Date mydate;
    private  String[] items={"All Categories","Deaths","Riots","Sexual Assalts" +
                "Property Loss","Government Forces"};

    public Ushahidi(){}

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
         //displayMainForm();

    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    /**
     * Exits the Ushahidi MIDlet
     *
     * @param exit
     */
    public void exitUshahidi(boolean exit) {
       notifyDestroyed();
       destroyApp(exit);
    }

//<editor-fold defaultstate="collapsed" desc=" Main form ">
    public void displayMainForm(){
        mainForm = new Form("Ushahidi");
        mainForm.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/ushahidilogo.png");
            btreport = (new Button("Add Incedent"));
            btview = (new Button("View Incedents"));
            btsettings = (new Button("Change Settings"));

         //forms

        lbseparator=new Label("    ");
                    lblogo = new Label(imglogo);
        lblogo.setAlignment(Component.CENTER);

        //buttons

        btreport.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displayReportForm();
          }
        });

        btview.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displayViewForm();
          }
        });

        btsettings.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            displaySettingsForm();
          }
        });
        //commands
        //textfields

        //containers
        Container mainmenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container textbox = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        mainmenu.addComponent(lblogo);
        mainmenu.addComponent(lbseparator=new Label("    "));

        mainmenu.addComponent(btreport);
        mainmenu.addComponent(lbseparator=new Label("    "));
        mainmenu.addComponent(btview);
        mainmenu.addComponent(lbseparator=new Label("    "));
        mainmenu.addComponent(btsettings);
        mainForm.addComponent(BorderLayout.CENTER, mainmenu);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        Command exitCommand = new Command("Exit");
        mainForm.addCommand(exitCommand);
        mainForm.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                exitUshahidi(true);
            }
       });

        mainForm.show();
    }
     //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" View incident ">
    public void displayViewForm(){
        try {
            Container cate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            Container mainmenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            final Container eventlist = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            imglogo = Image.createImage("/ushahidi/res/map.png");

            final String [] allcategories={"Death in kiambu","Riots in UNOBI","A Child is mo...","My pen is stolen","Government is..."};
            final String [] deaths={"Death in kiambu","Death in Umoja","Death in westlands"};
            final String [] riots={"Riots in UNOBI","Riots in ANU","Riots in USIU"};
            final String [] sexual={"A Child is mo...","A Boy is seduces","a woman caught..."};
            eventlists=new List();

            eventlists=new List(allcategories);          

             eventlist.addComponent(eventlists);
             viewForm = new Form("View Incedents");
             viewForm.setLayout(new BorderLayout());
             viewForm.setScrollable(false);
             viewForm.setTransitionOutAnimator(
             CommonTransitions.createSlide(
             CommonTransitions.SLIDE_HORIZONTAL, true, 500));

             viewForm.setTransitionInAnimator(
             CommonTransitions.createSlide(
             CommonTransitions.SLIDE_HORIZONTAL, true, 500));

             tp = new TabbedPane();

            lbmap=(new Label(imglogo));
            mapcategory=new ComboBox(items);
            cate.addComponent(mapcategory);
            mainmenu.addComponent(lbmap);
            //mainmenu.addComponent(eventlists);

            tp.addTab("Incedent Map",mainmenu);
            tp.addTab("Incedent List",eventlist);
//            tp.addTab("Calender", new Calendar());

            viewForm.addComponent(BorderLayout.NORTH,cate);
            viewForm.addComponent("Center", tp);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        viewForm.show();
        viewForm.addCommand(new Command("Back") {
             public void actionPerformed(ActionEvent ev) {
                    displayMainForm();
                }
        });

        viewForm.addCommand(new Command("View") {
            public void actionPerformed(ActionEvent ev) {
                displaydetalis();
            }
        });

    }
      //</editor-fold>
     //<editor-fold defaultstate="collapsed" desc=" display settings ">
    public void displaySettingsForm(){
        String [] instances={"Mogadishu","Haiti","Map Kibera","Post Election"};

         settingsForm = new Form("Change Settings");
         settingsForm.setLayout(new BorderLayout());

         instanceTextField = new ComboBox(instances);
         reportsTextField = new TextField();
         firstNameTextField = new TextField();
         lastNameTextField = new TextField();
         emailTextField = new TextField();

         Container formComponents = new Container(new BoxLayout(BoxLayout.Y_AXIS));
         formComponents.addComponent(new Label("Instance"));
         formComponents.addComponent(instanceTextField);
         formComponents.addComponent(new Label("No. of instances"));
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
             }
         });

         settingsForm.addCommand(new Command("Add incidence") {
             public void actionPerformed(ActionEvent ev) {
                addinstance();
             }
         });

         settingsForm.show();


    }
      //</editor-fold>
     //<editor-fold defaultstate="collapsed" desc=" Report incident ">
    public void displayReportForm(){

        mydate=new Date();
        String stringdate=mydate.toString();
        String [] dateony=split(stringdate," ");
        String today=dateony[0]+" "+dateony[1]+" "+dateony[2]+" "+dateony[3]+" "+dateony[5];

        reportForm = new Form("Add Incedents");
        reportForm.setLayout(new BorderLayout());
        reportForm.setTransitionInAnimator(
        CommonTransitions.createSlide(
        CommonTransitions.SLIDE_HORIZONTAL, true, 500));
        reportForm.setTransitionOutAnimator(
        CommonTransitions.createSlide(
        CommonTransitions.SLIDE_HORIZONTAL, true, 500));
        try {
            imglogo = Image.createImage("/ushahidi/res/smallogo.png");
            lblogo = new Label(imglogo);
            lblogo.setAlignment(Component.CENTER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        txtitle = new TextField();
        txdescri=new TextArea();
        txdescri=new TextArea(3, 20);
        txlocation = new TextField();
        txdate = new TextField(today);

        category=new ComboBox(items);


        Container buttonBar = new Container(new BoxLayout(BoxLayout.X_AXIS));
        Container textbox = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        takephoto = (new Button("Take Photo"));
        takegallary = (new Button("From Gallery"));

        buttonBar.addComponent(takephoto);
        buttonBar.addComponent(takegallary);
        textbox.addComponent(lblogo);
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

        reportForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displayMainForm();
            }
        });

        reportForm.addCommand(new Command("Submit") {
            public void actionPerformed(ActionEvent ev) {

            }
        });

    }
     //</editor-fold>
     //<editor-fold defaultstate="collapsed" desc=" display the story">

    public void displaydetalis() {
        detailsForm = new Form("Incedent Details");
        detailsForm.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/ushahidilogo.png");
            lblogo = new Label(imglogo);
            lblogo.setAlignment(Component.CENTER);

            Container mainmenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            mainmenu.addComponent(lblogo);
            mainmenu.addComponent(new TextArea("Individual detailed incidences will be displayed here.\nYes here!!!"));
            detailsForm.addComponent(BorderLayout.NORTH, mainmenu);

       } catch (IOException ex) {
            ex.printStackTrace();
       }

        detailsForm.show();

        detailsForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displayViewForm();
            }
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" add an instance">

    public void addinstance() {
        instance = new Form("Add an Instance");
        instance.setLayout(new BorderLayout());

        try {
            imglogo = Image.createImage("/ushahidi/res/ushahidilogo.png");
            lblogo = new Label(imglogo);
            lblogo.setAlignment(Component.CENTER);

            Container mainmenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            
instancename = new TextField();
instanceurl = new TextField();
         mainmenu.addComponent(lblogo);
         mainmenu.addComponent(new Label("Instance Name"));
         mainmenu.addComponent(instancename);
           mainmenu.addComponent(new Label("Instance Url"));
         mainmenu.addComponent(instanceurl);

            instance.addComponent(BorderLayout.NORTH, mainmenu);

       } catch (IOException ex) {
            ex.printStackTrace();
       }

        instance.show();

        instance.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                displaySettingsForm();
            }
        });
         instance.addCommand(new Command("Submit") {
            public void actionPerformed(ActionEvent ev) {

            }
        });

    }
    //</editor-fold>

//    private void

    /**
     * Performs a connection test
     *
     * @return true if connected & false if not
     */
    private boolean connectionTest() {
        return (new UshahidiInstance().isConnectionAvailable());
    }

    /**
     *Checks if there is a data connection as it displays the
     * Splash screen.
     *
     * @return No value is returned.
     */

   //<editor-fold defaultstate="collapsed" desc=" display SplashScreens ">
    private void showSplashScreen() {

        splashForm = new Form();
        splashForm.setLayout(new BorderLayout());
        Command exitCommand = new Command("Exit");
        splashForm.addCommand(exitCommand);
        splashForm.setBackCommand(exitCommand);
        splashForm.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                exitUshahidi(true);
            }
        });

        try {
            Image splashImage = Image.createImage("/ushahidi/res/splash.jpg");
            splashForm.getStyle().setBgImage(splashImage);
        } catch (Exception e) {
        }

        splashForm.show();

        //Performing a connection test
        if(connectionTest()) {
            splashForm.setTransitionOutAnimator(CommonTransitions.createSlide(
                    CommonTransitions.SLIDE_VERTICAL, false, 300));
            displayMainForm();
        } else {
            boolean action = Dialog.show("Connection error", "There is no active data connection " +
                    "\n Please check your phone internet settings\n" +
                    " or \n check your credit account.", "Continue Anyway", "Retry");
if (action==true){
displayMainForm();
}
else{
showSplashScreen();
}

System.out.println(action);
        }
    }
     //</editor-fold>
 //<editor-fold defaultstate="collapsed" desc="string splitter ">
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
}