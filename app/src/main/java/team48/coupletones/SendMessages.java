package team48.coupletones;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.InetAddress;

/**
 * Created by Zhuyupei on 5/6/16.
 */
//A class to help send messages
public class SendMessages {
    private String partnerKey;
    private String API = "AIzaSyC0lPo9sW8gNCe99A5CXjoXpIspBz4gV2g";
    private String message;
    private String title;
    static final public String PAIRED_UP = "team48.coupletones.SendMessages.PAIR";
    static final public String BREAK_UP = "team48.coupletones.SendMessages.BREAK";
    static final public String PAIRED_UP_FAILURE = "team48.coupletones.SendMessages.PAIR_F";
    static final public String BREAK_UP_FAILURE = "team48.coupletones.SendMessages.BREAK_F";
    GoogleCloudMessaging gcm;
    Boolean sentSuccessful;

    // constructor
    public SendMessages(String partnerKey, GoogleCloudMessaging gcm){
        this.partnerKey = partnerKey;
        this.gcm = gcm;
    }

    // Send a pair up message
    public boolean sendPairUpMessage(String myKey){
        return sendMessage(PAIRED_UP, myKey);
    }
    // Send a break up message
    public boolean sendBreakUpMessage(String myKey){
        return sendMessage(BREAK_UP, myKey);
    }

    // Send a break up failure message
    public boolean sendBreakUpFailureMessage(){
        return sendMessage(BREAK_UP_FAILURE, "");
    }

    // Send a pair up failure message
    public boolean sendPairUpFailureMessage(){
        return sendMessage(PAIRED_UP_FAILURE, "");
    }



    // Send a message
    public boolean sendMessage(String sendtitle,String sendmessage){
        this.title = sendtitle;
        this.message = sendmessage;
        try{
        return new AsyncTask<Void,Void,Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    Content c = new Content();
                    c.addRegId(partnerKey);
                    c.createData(title, message);
                    return Post2Gcm.post(API, c);
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean msg) {
                sentSuccessful = msg;
            }
        }.execute(null, null, null).get();
        }catch(Exception e){
            return false;
        }
    }
}
