package com.mycompany.whistleblowerclient2.panes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author tethik
 */
public class MainWindowController implements Initializable {    
    
    @FXML public Label labelTitle;
    @FXML public Pane paneMain;

    /**
     * initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        
        PanePage.setMainWindow(this);
        registerPage("buttonLeaks", new LeaksListPaneController());
        registerPage("buttonSettings", new SettingsPaneController());
        
        
//        try {
               //        pageTitles.put("buttonLeaks", "Leaks");
//        pageTitles.put("buttonKey", "Key Management");
//        pageTitles.put("buttonStatus", "Status");
//        pageTitles.put("buttonSettings", "Settings");
//        

//           changePage("buttonLeaks");

//        } catch (IOException ex) {
//            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }    
    
    protected void registerPage(String id, PanePage page) {
        pageTitles.put(id, page.getTitle());
        pageXMLs.put(id, page.getFXML());
    }
    
    private HashMap<String, String> pageTitles = new HashMap<>();
    private HashMap<String, String> pageXMLs = new HashMap<>();
    private HashMap<String, Pane> paneCache = new HashMap<>();
    private HashMap<String, PanePage> pgCache = new HashMap<>();
    
    private Pane lazyLoadPane(String id) throws IOException {
        String xmlFile = pageXMLs.get(id);
        if(xmlFile == null) {
            return new AnchorPane();
        }
        if(!paneCache.containsKey(id)) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/"+xmlFile
                    )
            );

            Pane page = loader.load();
            pgCache.put(id, loader.getController());
            paneCache.put(id, page);

        }
        return paneCache.get(id);        
    }
    
    void changePage(Pane pane) {
        paneMain.getChildren().clear();
        paneMain.getChildren().add(pane);  
    }
    
    void changePage(String id) throws IOException {        
        labelTitle.setText(pageTitles.get(id));
        Pane pane = lazyLoadPane(id);
        changePage(pane);
        if(pgCache.get(id) != null) {
            pgCache.get(id).onload();
        }
    }
    
    @FXML protected void handleMenuButtonAction(ActionEvent event) throws IOException {  
        Button source = (Button) event.getSource();
        String id = source.idProperty().get();
        changePage(id);
    }
    
    
}
