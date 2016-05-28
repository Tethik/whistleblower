package whistleblowerclient.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Created by tethik on 05/01/16.
 */
public class SubmissionFile {


    public SubmissionFile() {

    }
    public String filename;
    public String binary;

    public void saveToFolder(String path) throws IOException {
        // todo: sanitize filename
        FileOutputStream out = new FileOutputStream(path + "/" + filename);
        out.write(Base64.getDecoder().decode(binary));
        out.close();
    }

}
