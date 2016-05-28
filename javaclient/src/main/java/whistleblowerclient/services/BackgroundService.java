package whistleblowerclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import whistleblowerclient.Log;
import whistleblowerclient.crypto.CryptographyHandler;
import whistleblowerclient.crypto.PGPDecryptionResult;

import java.io.*;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Polls for leaks
 * @author tethik
 */
public class BackgroundService implements Runnable {
        
    private SubmissionList currentListOfFiles;
    private static final String DOWNLOAD_DIRECTORY = "downloads/";

    private WhistleblowerDirectoryAPI api = WhistleblowerDirectoryAPI.getInstance();
    private CryptographyHandler ch = CryptographyHandler.getInstance();
    private DownloadedSubmissions db = DownloadedSubmissions.getInstance();
    
    public BackgroundService() {
        loadCachedSubmissionList();

    }
    

    
    private void loadCachedSubmissionList() {
        ObjectMapper mapper = new ObjectMapper();                   
        try {
            FileInputStream io = new FileInputStream("submissions.json");
            currentListOfFiles = mapper.readValue(io, SubmissionList.class);
        } catch (FileNotFoundException ex) {
            Log.i("Previous submission file not found");
            currentListOfFiles = new SubmissionList();
            saveCacheFile();
        } catch (IOException ex) {
            Log.e("Submissions.json seems to be corrupted, json could not be mapped to object");
            currentListOfFiles = new SubmissionList();
            saveCacheFile();
        }        
    }
   
    
    private List<String> compare(SubmissionList response) {
        List<String> newFiles = new ArrayList<>();

        String firstItemFromPreviousList = currentListOfFiles.files.isEmpty() ? null : currentListOfFiles.files.get(currentListOfFiles.files.size()-1);
        for(String file : response.files) {

            // Lazy, break on first match from previous list.
            if(file.equals(firstItemFromPreviousList)) {
                break;
            } else {                               
                Log.i("New file: " + file);   
                newFiles.add(file);
            }
        }

        return newFiles;
    }

    private void download(String id) {
        File f = new File(DOWNLOAD_DIRECTORY + id + "/"); // Should be sanitized
        f.mkdirs();
        InputStream in = null;
        try {
            in = api.DownloadFile(id+".asc");

            // Save to file, for backup.
            File encrypted = new File(f.toPath()+"/"+id+".asc");
            Files.copy(in, encrypted.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Log.i("Saved encrypted contents to " + encrypted.toPath());

        } catch (IOException e) {
            Log.e(e.toString());
        } finally {
            if(in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean decrypt(String id) throws IOException {
        InputStream in = new FileInputStream(DOWNLOAD_DIRECTORY + id + "/" + id + ".asc");
        PGPDecryptionResult result = ch.decrypt(in);
        in.close();

        File f = new File(DOWNLOAD_DIRECTORY + id + "/" + id + ".json");

        if(result.decryptedSuccessfully) {
            OutputStream out = new FileOutputStream(f, false);
            ObjectMapper mapper = new ObjectMapper();
            result.submission.received = Calendar.getInstance().getTime().toString();
            mapper.writeValue(out, result.submission);
            Log.i(result.submission.received);
            out.close();
            return true;
        }
        
        f.delete();

        return false;
    }

    private void unpack(String id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        File f = new File(DOWNLOAD_DIRECTORY + id + "/" + id + ".json");
        Submission submission = mapper.readValue(f, Submission.class);
        String path = DOWNLOAD_DIRECTORY + id + "/";
        for(SubmissionFile file : submission.files) {
            Log.i("Unpacking " + file);
            file.saveToFolder(path);
        }
        submission.files.clear();
        mapper.writeValue(new File(DOWNLOAD_DIRECTORY + id + "/meta.json"), submission);
    }

    private void notify(String file) {
        for(BackgroundService.PollingServiceListener l : listeners) {
            l.newLeak(file);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                List<String> newFiles = compare(api.poll());

                for(String file : newFiles) {
                    String id = file.substring(0, file.indexOf("."));

                    download(id);

                    if(!decrypt(id)) {
                        Log.i("Failed to decrypt, likely the submission was not meant for us.");
                    } else {
                        Log.i("File downloaded and decrypted.");
                        unpack(id);
                        db.add(id);
                        notify(id);
                    }

                    // Finally done, update submission list
                    currentListOfFiles.files.add(file);
                    saveCacheFile();

                }
            } catch (SocketException ex) {
                Log.e("Could not open a SOCKS connection. Is Tor running?");
                Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        
    }
    
    public interface PollingServiceListener {
        public void newLeak(String file);
    }
    
    protected ArrayList<PollingServiceListener> listeners = new ArrayList<>();
    
    public void registerListener(PollingServiceListener listener) {
        listeners.add(listener);
    }
    
    private void saveCacheFile() {                
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("submissions.json"), currentListOfFiles);           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BackgroundService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    
}
