package team48.coupletones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
// interface for paired-up users.
public class PairedActivity extends AppCompatActivity {

    BroadcastReceiver receiver;
    GoogleCloudMessaging gcm;
    Button endRelation, map;

    @Override
    protected void onResume(){
        SharedPreferencesEditor editor = new SharedPreferencesEditor(getSharedPreferences("userInfo", MODE_PRIVATE));
        // When the activity resumes, check if the partner's API key exists.
        // If there is a key, they paired up when the activity was pausing.
        // Finish the activity.
        if(!editor.checkAPIKey()){
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register the receiver for getting notification from the GcmMessageHandler
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(GcmMessageHandler.NOTICE)
        );
    }

    @Override
    protected void onStop() {
        // Unregister the receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paired);

        // Get the GCM instance.
        gcm = GoogleCloudMessaging.getInstance(this);

        // Get the button endRelation
        endRelation = (Button) findViewById(R.id.bEndRelationship);

        // Get the button map
        map = (Button) findViewById(R.id.bMap);

        // Set onClickListener to endRelation button
        endRelation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Prompt an alert to user to make sure if he really wants to end relation
                AlertDialog.Builder alert = new AlertDialog.Builder(PairedActivity.this);

                alert.setMessage("Are you sure?");

                // Set the actions when user press yes. Send a break-up message to the partner
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Get partner's Key
                        SharedPreferencesEditor editor = new SharedPreferencesEditor(getSharedPreferences("userInfo",MODE_PRIVATE));
                        String key = new String(editor.getAPIKey());

                        // Try to send the break-up message.
                        if(!(new SendMessages(key, gcm).sendBreakUpMessage(editor.getMyKey()))){
                            // Something goes wrong. i.e. No Internet.
                            Toast.makeText(getApplicationContext(),"Error: Cannot delete right now" ,Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Delete partner's key locally
                        editor.deleteAPIKey();
                        // Go to solo activity
                        Intent goingSolo = new Intent(PairedActivity.this,SoloActivity.class);

                        startActivity(goingSolo);

                        finish();
                        // Prompt a message to encourage user to find a new partner
                        Toast.makeText(getApplicationContext(),"Find a new partner!",Toast.LENGTH_LONG).show();
                    }
                });
                // If user click on nope, nothing happens
                alert.setNegativeButton("Nope", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.create();

                alert.show();
            }
        });

        // When user click on map button, goes to the mapActivity
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PairedActivity.this, MapsActivity.class);

                startActivity(intent);

            }
        });

        // Set up the receiver. When the activity gets notified by GcmMessageHandler, the activity should do something
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Check if partner has deleted you.
                if(intent.getBooleanExtra(SendMessages.BREAK_UP, false)){
                    Toast.makeText(getApplicationContext(),"Sorry, you are deleted by your partner.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PairedActivity.this, SoloActivity.class));
                    finish();
                }
                // Check if the pair up fails.
                else if(intent.getBooleanExtra(SendMessages.PAIRED_UP_FAILURE, false)){
                    Toast.makeText(getApplicationContext(),"Error: Cannot pair up at this moment", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PairedActivity.this, SoloActivity.class));
                    finish();
                }
            }
        };
    }
}