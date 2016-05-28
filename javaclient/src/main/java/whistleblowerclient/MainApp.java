package whistleblowerclient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.openpgp.PGPException;
import whistleblowerclient.crypto.CryptographyHandler;
import whistleblowerclient.services.BackgroundService;
import whistleblowerclient.services.JavascriptVerifier;

import java.io.IOException;
import java.security.Security;

/**
 *
 * @author tethik
 */
public class MainApp extends Application {    
    
    BackgroundService backgroundService = new BackgroundService();
    
    @Override
    public void start(Stage primaryStage) throws IOException, PGPException {
        // Load crypto stuff
        CryptographyHandler.getInstance().loadKeys();


        TrayIcon icon = new TrayIcon();
        if(icon.addAppToTray()) {
            backgroundService.registerListener(icon);
        }

        // Polling service runs as a background service that periodically checks the server.
        Thread t = new Thread(backgroundService);
        t.setDaemon(true);
        t.start();


        Parent page = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
        Scene scene = new Scene(page, 1200, 900);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();

//        JavascriptVerifier verifier = new JavascriptVerifier();
//        Log.i(verifier.validate().toString());

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        launch(args);
    }
    
}
