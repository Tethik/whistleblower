/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whistleblowerclient.services;

import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author tethik
 */
public class Submission {

    public String description;
    public String short_title;
    public String contact;
    public String reply_to;
    public String sessionKey;

    public List<SubmissionFile> files = new ArrayList<>();
    public String received;

    public List<String> receivers = new ArrayList<>();

    public Submission() {

    }
    
}
