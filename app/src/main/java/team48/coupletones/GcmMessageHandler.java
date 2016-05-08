package team48.coupletones;



import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;
// Service to handle received messages from gcm
public class GcmMessageHandler extends IntentService{

    static int mNotificationId = 0;
    static final String NOTICE = "team48.coupletones.GcmMessageHandler.NOTICE";
    String mes;
    String tit;

    LocalBroadcastManager broadcaster;
    GoogleCloudMessaging gcm;
    SendMessages messages;
    SharedPreferencesEditor editor;

    // method to notify activities
    public void notifyActivities(String message) {
        Intent intent = new Intent(NOTICE);
        intent.putExtra(message, true);
        broadcaster.sendBroadcast(intent);
    }

    // constructor
    public GcmMessageHandler(){
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // initialize everything
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        editor = new SharedPreferencesEditor(getSharedPreferences("userInfo",MODE_PRIVATE));
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get the title and message
        Bundle extras = intent.getExtras();
        tit = extras.getString("title");
        mes = extras.getString("message");
        messages = new SendMessages(mes, gcm);

        // if message is about pair up
        if(tit.equals(SendMessages.PAIRED_UP)){
            // check if user is paired up
            if(editor.checkAPIKey()){
                messages.sendPairUpFailureMessage();
            }
            // add the key and paired up
            else{
                editor.addAPIKey(mes);
                notifyActivities(SendMessages.PAIRED_UP);
                showNotification("CoupleTones", "You are paired up!!");
            }
        }
        // if message is about break up
        else if(tit.equals(SendMessages.BREAK_UP)){
            // check if the key matches and if user is paired up.
            Log.i("pairup", mes+"\n"+editor.getAPIKey()+"\n"+editor.getAPIKey().equals(mes));
            if(!editor.getAPIKey().equals(mes)){
                messages.sendBreakUpFailureMessage();
            }
            // do the break up
            else{
                editor.deleteAPIKey();
                notifyActivities(SendMessages.BREAK_UP);
                showNotification("CoupleTones", "Sorry, you are deleted by your partner.");
            }
        }
        // if message is about paired up failure
        else if(tit.equals(SendMessages.PAIRED_UP_FAILURE)){
            editor.deleteAPIKey();
            notifyActivities(SendMessages.PAIRED_UP_FAILURE);
            showNotification("CoupleTones", "Cannot pair up your partner.");
        }
        // if message is about break up failure
        else if(tit.equals(SendMessages.BREAK_UP_FAILURE)){
            notifyActivities(SendMessages.BREAK_UP_FAILURE);
            showNotification("CoupleTones", "Cannot delete your partner.");
        }

        Log.i("GCM", "Received: (" + tit + ") " + mes);

        GcmReceiver.completeWakefulIntent(intent);
    }

    // method to show notifications.
    public void showNotification(String title, String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_2)
                        .setContentTitle(title)
                        .setContentText(message).setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationId += 1;

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
