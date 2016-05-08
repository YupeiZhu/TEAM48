package team48.coupletones;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/*
 * class that is copied from lab. Used to post messages to Gcm.
 */
public class Post2Gcm {
    public static boolean post(String apiKey, Content content) {
        try {

            URL url = new URL("https://android.googleapis.com/gcm/send");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();

            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            mapper.writeValue(wr, content);

            wr.flush();

            wr.close();

            int responseCode = conn.getResponseCode();
            Log.i("GCM","\nSending 'POST' request to URL:" + url);
            Log.i("GCM", "Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            in.close();
            if(response.toString().contains("\"failure\":1")){
                return false;
            }


            Log.i("GCM",response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

