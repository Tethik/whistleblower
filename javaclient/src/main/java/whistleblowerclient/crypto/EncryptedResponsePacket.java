package whistleblowerclient.crypto;

/**
 * Created by tethik on 10/01/16.
 */

/***
 * Packet that can be serialized into json that SJCL.js understands :)
 */
public class EncryptedResponsePacket {
    /**
     * The IV
     */
    public String iv;
    /**
     * The Ciphertext
     */
    public String ct;
    /**
     * The Block mode
     */
    public String mode;
    /**
     * Authenticated Data. Likely a hash to authenticate the ciphertext.
     */
    public String adata = "";
    /**
     * The cipher used
     */
    public String cipher = "aes";

}
