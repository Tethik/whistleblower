package whistleblowerclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * Created by tethik on 05/01/16.
 */
public class DownloadedSubmissions {

    public List<String> submissions = new Stack<>();

    private DownloadedSubmissions() {
        try {
            load();
        } catch(IOException ex) {
        }
    }

    private static DownloadedSubmissions instance = new DownloadedSubmissions();

    public static DownloadedSubmissions getInstance() {
        return instance;
    }


    public void add(String id) throws IOException {
        submissions.add(id);
        save();
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("saved.json"), this);
    }

    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        submissions = mapper.readValue(new File("saved.json"), this.getClass()).submissions;
    }

}
