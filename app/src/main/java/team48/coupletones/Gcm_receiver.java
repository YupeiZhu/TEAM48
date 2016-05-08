package team48.coupletones;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
/*
 * The receiver to handle the gcm message from the partner
 */
public class Gcm_receiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the message from context.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmMessageHandler.class.getName());
        // Start the service to handle the message.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}
