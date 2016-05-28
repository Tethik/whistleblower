package whistleblowerclient.crypto;

import whistleblowerclient.services.Submission;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tethik on 10/01/16.
 */
public class PGPDecryptionResult {
    public Submission submission;
    public boolean decryptedSuccessfully = false;

}
