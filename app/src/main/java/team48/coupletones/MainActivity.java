package team48.coupletones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread back = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                    Intent i = new Intent(getBaseContext(),CoupleActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
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
