package whistleblowerclient.crypto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tethik on 10/01/16.
 */
public class InnerResponsePacket {

    /**
     * The original list of receivers
     */
    public List<String> receivers = new ArrayList<>();

    /**
     *  The message contents.
     */
    public List<ReplyMessage> messages = new ArrayList<>();

}
