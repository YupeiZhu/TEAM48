package team48.coupletones;

import android.content.SharedPreferences;

//a class to edit SharedPreferences
public class SharedPreferencesEditor {

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    // Constructor
    public SharedPreferencesEditor( SharedPreferences sharedPreferences ){

        this.sharedPreferences = sharedPreferences;

        editor = sharedPreferences.edit();

    }

    // Add user's key to SharedPreferences
    public void addMyKey(String APIKey){

        editor.putString("myAPIKey", APIKey.trim());

        editor.apply();

    }

    // Get user's key from SharedPreferences
    public String getMyKey(){
        return sharedPreferences.getString("myAPIKey", "");

    }

    // Get partner's key from SharedPreferences
    public String getAPIKey(){
        return sharedPreferences.getString("partnerAPIKey", "");
    }


    // Add partner's key to SharedPreferences
    public void addAPIKey(String APIKey){

        editor.putString("partnerAPIKey", APIKey.trim());

        editor.apply();

    }


    //Delete partner's key
    public void deleteAPIKey(){

        editor.remove("partnerAPIKey");

        editor.apply();

    }

    //Check if user's key exist
    public boolean checkMyKey(){

        return sharedPreferences.contains("myAPIKey");

    }

    //Check if partner's key exist
    public boolean checkAPIKey(){

        return sharedPreferences.contains("partnerAPIKey");

    }
}