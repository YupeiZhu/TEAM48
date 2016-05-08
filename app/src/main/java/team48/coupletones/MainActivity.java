package team48.coupletones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
 * The initial Activity for loading.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Thread fetchLocation = new Thread(){
            public void run(){
                Intent serviceLocation = new Intent(MainActivity.this, MyLocationService.class);
                startService(serviceLocation);
            }
        };
        fetchLocation.start();

        // Create a thread that choose which activity to go in
        Thread back = new Thread(){

            public void run(){

                try{

                    sleep(3000);

                    SharedPreferencesEditor editor = new SharedPreferencesEditor(getSharedPreferences("userInfo",MODE_PRIVATE));
                    Intent locationService = new Intent(MainActivity.this, MyLocationService.class);
                    Log.i("abcd", "APIKey is "+ editor.getAPIKey() + " "+ editor.checkAPIKey());
                    if(editor.checkAPIKey()){
                        // if partner's APIKey exists, goes to PairedActivity
                        Intent i = new Intent(getBaseContext(),PairedActivity.class);

                        startActivity(i);

                    }

                    else{
                        // if partner's APIKey exists, goes to PairedActivity
                        Intent i = new Intent(getBaseContext(),SoloActivity.class);

                        startActivity(i);

                    }

                    finish();

                }

                catch (InterruptedException e) {

                    e.printStackTrace();

                }
            }
        };

        back.start();

    }



    protected void onDestroy(){

        super.onDestroy();

    }
}