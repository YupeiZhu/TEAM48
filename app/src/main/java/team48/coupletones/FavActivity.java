package team48.coupletones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.ArrayList;


public class FavActivity extends AppCompatActivity {


    Button button1;
    ListView scrollList;
    ArrayList<String> aList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        scrollList = (ListView) findViewById(R.id.listView);
        button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //map view appears
                finish();
            }
        });

        String favLocations = readFromFile();
        favList(favLocations);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aList);
        scrollList.setAdapter(adapter);



    }

    public String readFromFile() {

        String ret = "";

        try {

            InputStream inputStream = openFileInput("favoriteLocations.txt");

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {

                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }

        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void favList(String string){
        StringTokenizer st = new StringTokenizer(string);
        while(st.hasMoreTokens()){
            String location = st.nextToken() +",";
            location = location + st.nextToken();
            location = location + " " + st.nextToken();
            aList.add(location);
        }

    }
}
