package team48.coupletones;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CoupleActivity extends AppCompatActivity {

    Button button1, button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couple);

        button1 = (Button) findViewById(R.id.bEnd);
        button2 = (Button) findViewById(R.id.bMap);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CoupleActivity.this);
                alert.setMessage("Are you sure?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent goingSolo = new Intent(CoupleActivity.this,SoloActivity.class);
                        startActivity(goingSolo);
                        Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.create();
                alert.show();
            }
        });
    }
}
