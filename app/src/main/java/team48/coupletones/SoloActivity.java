package team48.coupletones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SoloActivity extends AppCompatActivity {

    Button button1, button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo);

        button1 = (Button) findViewById(R.id.bRequest);
        button2 = (Button) findViewById(R.id.bMap);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SoloActivity.this,PendingActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_SHORT).show();
            }
        });
    }
}