import junit.framework.TestCase;
import org.bouncycastle.openpgp.PGPException;
import whistleblowerclient.crypto.CryptographyHandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Created by tethik on 04/01/16.
 */
public class CryptographyHandlerTest extends TestCase {

    public void testKeygen() throws PGPException, NoSuchAlgorithmException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        CryptographyHandler ch = new CryptographyHandler();

        ch.keygen("test identity");
//        ch.keygen("test identity", "with password".toCharArray());
    }

    public void testLoadKeys() throws IOException, PGPException {
        CryptographyHandler ch = new CryptographyHandler();
        ch.loadKeys();
    }

    public void testDecryption() {

    }
}
