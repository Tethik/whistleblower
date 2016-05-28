/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whistleblowerclient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tethik
 */
public class Log {
    
    private static String getCallingClassName() {
        return Thread.currentThread().getStackTrace()[1].getClassName();
    }
    
    
    public static void i(String message) {
        Logger.getLogger(getCallingClassName()).log(Level.INFO, message);
    }
    
    public static void e(String message) {
        Logger.getLogger(getCallingClassName()).log(Level.SEVERE, message);
    }
    
   
    
}
