/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whistleblowerclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import whistleblowerclient.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tethik
 */
public class WhistleblowerDirectoryAPI {
    
    private Proxy proxy;

    private static final boolean DEBUG_USE_TOR = false;
    private static String SERVICE_URL = "http://127.0.0.1:5011/api";
//    private static String SERVICE_URL = "http://jzkelb5g73g6q3wn.onion/api";
    private static WhistleblowerDirectoryAPI api = new WhistleblowerDirectoryAPI();
    
    private WhistleblowerDirectoryAPI() {
        if(DEBUG_USE_TOR) {
            setUpTorProxy();
        }
    }
    
    public static WhistleblowerDirectoryAPI getInstance() {
        return api;
    }

    private URLConnection openUrl(String url) throws IOException {
        URL u = new URL(url);
        if(!DEBUG_USE_TOR) {
            SERVICE_URL = "http://127.0.0.1:5011/api";
            return u.openConnection();
        }

        return u.openConnection(proxy);
    }
    
    private void setUpTorProxy() {
        // Connect to tor
        SocketAddress addr = new InetSocketAddress("127.0.0.1", 9050);
        proxy = new Proxy(Proxy.Type.SOCKS, addr);
    }
    
    public SubmissionList poll() throws IOException {
        try {
            URLConnection connection = openUrl(SERVICE_URL+"/submissions");
            connection.setDoOutput(true);            
            
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = connection.getInputStream();
            SubmissionList response = mapper.readValue(is, SubmissionList.class);

            is.close();
            return response;
        } catch (MalformedURLException ex) {
            Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public InputStream DownloadFile(String file) throws IOException {
        try {
            URLConnection connection = openUrl(SERVICE_URL+"/submissions/"+file);
            connection.setDoOutput(true);

            return connection.getInputStream();
        } catch (MalformedURLException ex) {
            Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public InputStream GetReply(String id) throws IOException {
        id = URLEncoder.encode(id, "UTF-8");
        URLConnection connection = openUrl(SERVICE_URL+"/fetch_reply/"+id);
        return connection.getInputStream();
    }

    public InputStream PostReply(String id, String encryptedMessage) throws IOException {
        id = URLEncoder.encode(id, "UTF-8");
        HttpURLConnection connection = (HttpURLConnection) openUrl(SERVICE_URL+"/reply_to/"+id);
        connection.setRequestMethod("POST");

        Log.i(encryptedMessage);
        String data = String.format("content=%s", URLEncoder.encode(encryptedMessage, "UTF-8"));
        Log.i(data);
        byte[] postDataBytes = data.getBytes("UTF-8");



        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        connection.setRequestProperty( "charset", "utf-8");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();


        return connection.getInputStream();
    }
    
}
