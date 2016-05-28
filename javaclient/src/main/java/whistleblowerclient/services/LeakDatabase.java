/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whistleblowerclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * A local database for leaks.
 * @author tethik
 */
public class LeakDatabase {
    
    public LeakDatabase() {
        
    }
    
    public SubmissionList getList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("submissions.json");
            if(!file.exists())
                return new SubmissionList();
            return mapper.readValue(file, SubmissionList.class);
        } catch (IOException ex) {
            Logger.getLogger(LeakDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return new SubmissionList();
        }
    }
    
    public Submission get(String id) {
        return new Submission();
    }
    
}
