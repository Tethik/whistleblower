/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whistleblowerclient.services;

import java.util.List;
import java.util.Stack;

/**
 * POJO Class for submission list.
 * @author tethik
 */
public class SubmissionList {
    public List<String> files = new Stack<>();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(String file : files) {
            builder.append(file).append("\n");
        }
        return builder.toString();        
    }
}
