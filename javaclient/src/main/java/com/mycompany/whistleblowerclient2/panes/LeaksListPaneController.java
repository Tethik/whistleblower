/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.whistleblowerclient2.panes;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import whistleblowerclient.services.DownloadedSubmissions;
import whistleblowerclient.services.LeakDatabase;
import whistleblowerclient.services.SubmissionList;

/**
 * FXML Controller class
 *
 * @author tethik
 */
public class LeaksListPaneController extends PanePage implements Initializable {

    @FXML protected ListView<Pane> listLeaks;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    

    @Override
    public String getTitle() {
        return "Leaks";
    }

    @Override
    public String getFXML() {
        return "LeaksListPane.fxml";
    }

    @Override
    public void onload() {
        listLeaks.getItems().clear();

        DownloadedSubmissions ds = DownloadedSubmissions.getInstance();
        for(int i = ds.submissions.size() -1; i >= 0; --i) {
            String file = ds.submissions.get(i);
            Pane pane = new AnchorPane();
            HBox box = new HBox();
            box.setMinHeight(50);
            box.setPadding(new Insets(20,50,20,50));
            Insets itemSpacing = new Insets(0,15,0,15);
            Label label = new Label(file);
            label.setPadding(itemSpacing);
            Label dateLabel = new Label("Received 2015-12-12");
            dateLabel.setPadding(itemSpacing);
            box.getChildren().add(label);
            box.getChildren().add(dateLabel);

            Button button = new Button("Open");
            box.getChildren().add(button);
            pane.getChildren().add(box);
            listLeaks.getItems().add(pane);

            button.setOnAction((ActionEvent event) -> {
                try {
                    LeakPaneController controller = new LeakPaneController(file);

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/LeakPane.fxml"
                            )
                    );

                    loader.setController(controller);

                    changePane(loader.load());
                } catch (IOException ex) {
                    Logger.getLogger(LeaksListPaneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }


}
