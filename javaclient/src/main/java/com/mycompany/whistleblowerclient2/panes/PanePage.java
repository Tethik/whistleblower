/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.whistleblowerclient2.panes;

import javafx.scene.layout.Pane;

/**
 *
 * @author tethik
 */
public abstract class PanePage {
    
    public abstract String getTitle();    
    public abstract String getFXML();
    
    protected static MainWindowController mainWindow;
    
    public static void setMainWindow(MainWindowController mainWindow) {
        PanePage.mainWindow = mainWindow;
    }
    
    protected static void changePane(Pane pane) {
        mainWindow.changePage(pane);
    }

    public abstract void onload();
    
}
