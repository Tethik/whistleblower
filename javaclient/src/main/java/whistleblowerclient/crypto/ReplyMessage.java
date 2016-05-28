package whistleblowerclient.crypto;

/**
 * Created by tethik on 10/01/16.
 */
public class ReplyMessage {

    /**
     * The text of the message
     */
    public String text;

    /**
     * The KeyID of the responding journalist (for use in singular replies). Not very secure, for now.
     */
    public String responder;

    public ReplyMessage() {}

    public ReplyMessage(String responder, String text) {
        this.responder = responder;
        this.text = text;
    }
}
