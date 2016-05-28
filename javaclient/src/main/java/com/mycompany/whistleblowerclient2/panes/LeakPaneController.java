/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.whistleblowerclient2.panes;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import whistleblowerclient.Log;
import whistleblowerclient.crypto.CryptographyHandler;
import whistleblowerclient.crypto.EncryptedResponsePacket;
import whistleblowerclient.crypto.InnerResponsePacket;
import whistleblowerclient.crypto.ReplyMessage;
import whistleblowerclient.services.Submission;
import whistleblowerclient.services.WhistleblowerDirectoryAPI;

import javax.crypto.NoSuchPaddingException;

/**
 * FXML Controller class
 *
 * @author tethik
 */
public class LeakPaneController implements Initializable {

    private String id;
    private Submission meta;
    private InnerResponsePacket innerResponsePacket = null;

    @FXML
    public Label labelTitle;

    @FXML
    public Label labelDescription;

    @FXML
    public Label labelReceivers;

    @FXML
    public Label labelReceived;

    @FXML
    public Label labelContact;

    @FXML
    public Label labelCodename;

    @FXML
    public ListView<HBox> listFiles;

    @FXML
    public ListView<HBox> listMessages;

    @FXML
    public TextArea textReply;



    @FXML
    public Button buttonReply;

    private WhistleblowerDirectoryAPI api = WhistleblowerDirectoryAPI.getInstance();
    private CryptographyHandler ch = CryptographyHandler.getInstance();


    public LeakPaneController(String id) {
        this.id = id;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String folder = "downloads/"+id+"/";

        ObjectMapper mapper = new ObjectMapper();
        try {
            meta = mapper.readValue(new File(folder + "meta.json"), Submission.class);
            labelTitle.setText("Title: " + meta.short_title);
            labelDescription.setText("Description: " + meta.description);
            labelReceivers.setText("Receivers: " + String.join(",", meta.receivers));
            labelReceived.setText("Received: " + meta.received);
            labelCodename.setText("Codename: " + meta.reply_to);
            labelContact.setText("Contact: " + meta.contact);

            for (final File fileEntry : new File(folder).listFiles()) {
                if (fileEntry.getName().contains(id) || fileEntry.getName().equals("meta.json"))
                    continue;

                HBox box = new HBox();
                box.setMinHeight(25);
                box.setPadding(new Insets(10,10,10,10));
                Label label = new Label(fileEntry.getName());
                box.getChildren().add(label);
                listFiles.getItems().add(box);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        buttonReply.setOnAction(event -> reply());

        try {
            fetch(meta.reply_to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addChatMessage(ReplyMessage msg) {
        HBox box = new HBox();
        box.setMinHeight(25);
        box.setPadding(new Insets(10,10,50,10));
        Label label = new Label(msg.responder);
        label.setPadding(new Insets(10,10,10,10));
        Label textLabel = new Label(msg.text);
        textLabel.setPadding(new Insets(10,10,10,10));
        box.getChildren().add(label);
        box.getChildren().add(textLabel);
        listMessages.getItems().add(box);
    }

    private void fetch(String codename) throws IOException {
        try {
            InputStream in = api.GetReply(codename);
            innerResponsePacket = ch.decryptReply(meta.sessionKey, in);

            innerResponsePacket.messages.stream().forEach(replyMessage -> addChatMessage(replyMessage));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchProviderException e) {
            e.printStackTrace();
        } catch(FileNotFoundException ex) {
            Log.i("No reply yet.");
        }

    }

    private void reply()  {
        String text = textReply.getText();
        Log.i(text);
        ObjectMapper mapper = new ObjectMapper();

        if(innerResponsePacket == null) {
            innerResponsePacket = new InnerResponsePacket();
        }

        innerResponsePacket.receivers = meta.receivers;

        ReplyMessage msg = new ReplyMessage(ch.getUserID(), text);
        innerResponsePacket.messages.add(msg);
        String json = null;
        try {
            json = mapper.writeValueAsString(innerResponsePacket);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        try {
            EncryptedResponsePacket packet = ch.encryptReply(meta.sessionKey, stream);

            json = mapper.writeValueAsString(packet);
            Log.i(json);
            api.PostReply(meta.reply_to, json);
            addChatMessage(msg);
            textReply.setText("");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException | NoSuchProviderException e) {
            textReply.setText("Failed: " + e.toString());
        }


    }
    
}
