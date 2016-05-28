/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.whistleblowerclient2.panes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author tethik
 */
public class SettingsPaneController extends PanePage implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public String getFXML() {
        return "SettingsPane.fxml";
    }

    @Override
    public void onload() {

    }

}
