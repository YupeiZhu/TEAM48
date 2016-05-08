package team48.coupletones;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

//interface when not paired up with anyone
public class SoloActivity extends AppCompatActivity {

    //declaring three buttons
    Button addPartner, map, getRegId;
    TextView regId;
    EditText partnerId;
    String token = "";
    GoogleCloudMessaging gcm;
    BroadcastReceiver receiver;
    SharedPreferencesEditor editor;
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
    protected void onResume(){
        editor = new SharedPreferencesEditor(getSharedPreferences("userInfo", MODE_PRIVATE));
        // When the activity resumes, check if the partner's API key exists.
        // If there is not a key, they broke up when the activity was pausing.
        // Finish the activity.
        if(editor.checkAPIKey()){
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // get a GCM instance
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        setContentView(R.layout.activity_solo);

        editor = new SharedPreferencesEditor(getSharedPreferences("userInfo", MODE_PRIVATE));

        // assign a textView to regId
        regId = (TextView) findViewById(R.id.tvMyID);
        // assign a editText to partnerId
        partnerId = (EditText) findViewById(R.id.etPartnerID);

        //assigning a button for adding a partner
        addPartner = (Button) findViewById(R.id.bAddPartner);

        //assigning a button for switching to a map view
        map = (Button) findViewById(R.id.bMap);

        //assigning a button for getting my device id
        getRegId = (Button) findViewById(R.id.bGetMyID);

        //if "Add Partner" button is pressed
        addPartner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // when the key in the partnerId is the user's
                if(!editor.getMyKey().equals("")&&partnerId.getText().toString().equals(editor.getMyKey())){
                    Toast.makeText(getApplicationContext(),"Error: Unnecessary to pair up with your own device!",Toast.LENGTH_LONG).show();
                    return;
                }

                // add the key to SharedPreference
                editor.addAPIKey(partnerId.getText().toString());

                // create a SendMessage instance to send messages
                SendMessages messages = new SendMessages(editor.getAPIKey(), gcm);
                // Check if user is registered.
                if(!editor.checkMyKey()){
                    Toast.makeText(getApplicationContext(),"Error: Registration required!",Toast.LENGTH_SHORT).show();
                    editor.deleteAPIKey();
                }
                // Check if the message goes to partner successfully
                else if (messages.sendPairUpMessage(editor.getMyKey())) {
                    Intent intent = new Intent(SoloActivity.this, PairedActivity.class);
                    startActivity(intent);
                    finish();
                }
                // Handle about wrong registration Id or no internet
                else {
                    Toast.makeText(getApplicationContext(),"Error: No connectivity / Wrong ID",Toast.LENGTH_LONG).show();
                    editor.deleteAPIKey();
                }

            }
        });

        //if "MAP" button is pressed
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //map view appears
                Intent intent = new Intent(SoloActivity.this, MapsActivity.class);

                startActivity(intent);

            }
        });

        //if "Get My Device ID" button is pressed
        getRegId.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //initializing a TextView field
                getRegId();
            }
        });

        // Set up the receiver. When the activity gets notified by GcmMessageHandler, the activity should do something
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // If an other user pairs the user up.
                if(intent.getBooleanExtra(SendMessages.PAIRED_UP, false)){
                    Toast.makeText(getApplicationContext(),"You are paired up!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SoloActivity.this, PairedActivity.class));
                    finish();
                }
                // If something went wrong with breaking up the other user.
                else if(intent.getBooleanExtra(SendMessages.BREAK_UP_FAILURE, false)){
                    Toast.makeText(getApplicationContext(),"Error: Cannot break up at this moment", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // Set the behavior that when the RegId is clicked, the id goes to clipboard
    public void SelectRegId(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(regId.getText());
        Toast.makeText(getApplicationContext(), "Register ID copied!", Toast.LENGTH_SHORT).show();
    }


    // Method to get a regId
    private void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    token = gcm.register("344209905483");
                    editor.addMyKey(token);
                    Log.i("GCM", "regId: " + token);
                } catch (Exception ex) {
                    token = "Error :" + ex.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String msg) {
                regId.setText(msg);
            }
        }.execute(null, null, null);
    }

}