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
import java.util.Date;
import javax.microedition.lcdui.Alert;
//import javax.microedition.lcdui.CommandListener;
//import javax.microedition.lcdui.DateField;
import javax.microedition.midlet.*;

/**
 * @author toshiba
 */

public class Ushahidi extends MIDlet implements ActionListener {
    public Form mainForm,reportForm,viewForm,settingsForm,detailsForm;
public Button btreport,btview,btsettings,takephoto,takegallary;
public Label lbtitle,lbdescri,lblocation,lbdate,lbcatego;
    public Alert startmessage;
    public Image imglogo;
    public Label lblogo,lblist1,lblist2,lblist3,lblist4,lblist5,lbmap,lbseparator;
    public TabbedPane tp = null;
    public List eventlists;
    public ComboBox category,mapcategory;
    public TextField txtitle,txlocation,txdate,txcatego;
    public TextArea txdescri;
    public Date mydate;
    public  String[] items={"All Categories","Deaths","Riots","Sexual Assalts" +
                "Property Loss","Government Forces"};
    
    public Ushahidi(){
         
    }
    public void startApp() {
         Display.init(this);
         try
        {
            Resources res = Resources.open("/res/Ushahidi.res");
            UIManager.getInstance().setThemeProps(res.getTheme("Ushahidi"));
        }
        catch(IOException ex)
        {
        }

displayMainForm();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void actionPerformed(ActionEvent ae) {
   notifyDestroyed();
   destroyApp(true);

  }
//<editor-fold defaultstate="collapsed" desc=" Main form ">
    public void displayMainForm(){

          

mainForm = new Form("Ushahidi");
         mainForm.setLayout(new BorderLayout());
         
          
           
 try {
            imglogo = Image.createImage("/ushahidilogo.png");
            btreport = (new Button("Add Incedents"));
            btview = (new Button("View Incedents"));
            btsettings = (new Button("Change Setings"));

 //forms

lbseparator=new Label("    ");
            lblogo = new Label(imglogo);
lblogo.setAlignment(Component.CENTER);

//buttons

btreport.addActionListener(new ActionListener() {

  public void actionPerformed(ActionEvent ae) {

displayreportForm();
  }
});

btview.addActionListener(new ActionListener() {

  public void actionPerformed(ActionEvent ae) {

displayviewForm();
  }
});

btsettings.addActionListener(new ActionListener() {

  public void actionPerformed(ActionEvent ae) {

displaysettingsForm();
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
            ex.printStackTrace();
        }
        
Command exitCommand = new Command("Exit");
   mainForm.addCommand(exitCommand);
   mainForm.setCommandListener(this);

 mainForm.show();
    }

     //</editor-fold>
 //<editor-fold defaultstate="collapsed" desc=" View incident ">
    public void displayviewForm(){
        try {
            Container cate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            Container mainmenu = new Container(new BoxLayout(BoxLayout.Y_AXIS));
final Container eventlist = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        imglogo = Image.createImage("/map.png");
       
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
tp.addTab("Calender", new Calendar());
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
    public void displaysettingsForm(){

         settingsForm = new Form("Change Settings");
            settingsForm.setLayout(new BorderLayout());

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

            settingsForm.show();
       

    }
      //</editor-fold>
     //<editor-fold defaultstate="collapsed" desc=" Report incident ">
    public void displayreportForm(){
mydate=new Date();
String today=mydate.toString();
         reportForm = new Form("Add Incedents");
          reportForm.setLayout(new BorderLayout());
                   reportForm.setTransitionInAnimator(
        CommonTransitions.createSlide(
          CommonTransitions.SLIDE_HORIZONTAL, true, 500));
                   reportForm.setTransitionOutAnimator(
        CommonTransitions.createSlide(
          CommonTransitions.SLIDE_HORIZONTAL, true, 500));
        try {
            imglogo = Image.createImage("/smallogo.png");
        
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

    }
     //</editor-fold>
     //<editor-fold defaultstate="collapsed" desc=" display details">
   
    public void displaydetalis() {

        detailsForm = new Form("Incedent Details");
        detailsForm.setLayout(new BorderLayout());
        try {
            imglogo = Image.createImage("/ushahidilogo.png");
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
                displayviewForm();
            }
        });

    }
    //</editor-fold>     
}
