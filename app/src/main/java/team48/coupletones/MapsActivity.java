package team48.coupletones;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // try to split methods for SRP

    private GoogleMap mMap;
    Marker searchMarker = null;
    LatLng sMarkerPosition;
    boolean sMarkSet = false;
    BroadcastReceiver receiver;
    Button favButton;
    ArrayList<String> getName;

    @Override
    protected void onStart() {
        super.onStart();
        // Register the receiver for getting notification from the GcmMessageHandler
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(GcmMessageHandler.NOTICE)
        );
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up the receiver. When the activity gets notified by GcmMessageHandler, the activity should do something
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // When partner break up with you
                if(intent.getBooleanExtra(SendMessages.BREAK_UP, false)){
                    Toast.makeText(getApplicationContext(),"Sorry, you are deleted by your partner.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MapsActivity.this, SoloActivity.class));
                    finish();
                }
                // When pairing failed. Partner has a partner.
                else if(intent.getBooleanExtra(SendMessages.PAIRED_UP_FAILURE, false)){
                    Toast.makeText(getApplicationContext(),"Cannot pair up your partner.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MapsActivity.this, SoloActivity.class));
                    finish();
                }
                // Something goes wrong when breaking up.
                else if(intent.getBooleanExtra(SendMessages.BREAK_UP_FAILURE, false)){
                    Toast.makeText(getApplicationContext(),"Cannot break up your partner.", Toast.LENGTH_SHORT).show();
                }else if(intent.getBooleanExtra(SendMessages.PAIRED_UP, false)){
                    Toast.makeText(getApplicationContext(),"You are paired up!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MapsActivity.this, PairedActivity.class));
                    finish();
                }
                // do something here.
            }
        };

        favButton = (Button)findViewById(R.id.bFavList);
        favButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!readFromFile().equals("")){
                    Intent favIntent = new Intent(MapsActivity.this, FavActivity.class);
                    startActivity(favIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Favorite Locations", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }//END OF ONCREATE

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // check permissions and set location settings
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        // else print error message (usually never pops up)
        else {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Or use LocationManager.GPS_PROVIDER
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if(lastKnownLocation == null) {

            // error is occuring because  getlastknownlocation is returning null
            // no previous location to return from emulator
            // works when connected to device with location services on
            Toast.makeText(this, "Current location unavailable", Toast.LENGTH_LONG).show();
        }
        else {

            LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 5000, null);
            //locationNotification(lastKnownLocation);
        }

        //locationNotification(lastKnownLocation);

        // string of latitudes and longitudes listed as favorites
        String favoriteLocations = readFromFile();

        if(favoriteLocations != null) {

            printLocations(favoriteLocations);
        }

        // sets dialog box if location needs to be removed
        clickMap();
        removePopup();
    }

    // opens a window with a yes or no confirmation to remove set when infowindow is pressed
    public void removePopup() {

        // try to set it so that when info window is clicked new activity pops up
        // with options to save, remove, name location
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(final Marker marker) {

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Remove")
                        .setMessage("Are you sure you want to remove this location?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // get position of infowindow's marker
                                LatLng position = marker.getPosition();
                                double latitude = position.latitude;
                                double longitude = position.longitude;
                                String name = marker.getTitle();

                                // make string of latitude and longitude
                                String latLng = String.valueOf(latitude) + " " + String.valueOf(longitude) + " " + name + " ";

                                // get file that coordinates are stored
                                File dir = getFilesDir();
                                File file = new File(dir, "favoriteLocations.txt");

                                // send string of location to remove and file to remove method
                                removeLocation(file, latLng);

                                // clear map of current markers
                                mMap.clear();

                                // get new file with new list of locations
                                String favoriteLocations = readFromFile();

                                // print new locations
                                if(favoriteLocations != null) {

                                    printLocations(favoriteLocations);
                                }


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }// END OF REMOVEPOPUP

    // takes in file with list of coordinates and string of latitude longitude to remove
    private void removeLocation(File file, String string) {

        if (!file.isFile()) {

            System.out.println("Parameter is not an existing file");
            return;
        }

        // convert current file to string
        String favorites = readFromFile();

        // delete file
        file.delete();

        // tokenize string
        StringTokenizer st = new StringTokenizer(favorites);

        while(st.hasMoreTokens()) {

            double latitude = Double.parseDouble(st.nextToken(" "));
            double longitude = Double.parseDouble(st.nextToken(" "));
            String name = st.nextToken(" ");

            // create a location of each latitude and longitude on the list
            String location = String.valueOf(latitude) + " " + String.valueOf(longitude) + " " + name + " ";

            // check if location matches the string of location to be removed
            if(!location.equals(string)) {

                // if not equal send coordinates to new file
                flushLocationsToDisk(latitude, longitude, name);
                //tests to see what strings are being compared
                System.out.println(location);
                System.out.println(string);
                System.out.println("not right location");
            }
            else {

                // else dont send coordinates so it isnt in the new file
                System.out.println("removed location");
            }
        }
    }//END OF REMOVELOCATION

    // tokenizes string and parses out individual latitude and longitudes
    // and then puts all the favorited markers on the map
    private void printLocations(String string) {

        StringTokenizer st = new StringTokenizer(string);

        while (st.hasMoreTokens()) {
            String lon = ""; String name = "";
            String lat = st.nextToken(" ");
            if(st.hasMoreTokens()){
                lon = st.nextToken(" ");}
            if(st.hasMoreTokens()){
                name = st.nextToken(" ");}

            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lon);

            LatLng latLng = new LatLng(latitude, longitude);
            addMarker(mMap, latLng, name, true);
            addMarker(mMap, latLng, "Remove?", true);

        }
    }//END OF PRINTLOCATION

    // given a latitude and longitude, saves it as a string to a file which stores list
    // of favorites
    private void flushLocationsToDisk(double latitude, double longitude, String name){
        try {

            FileOutputStream fos = openFileOutput("favoriteLocations.txt", Context.MODE_APPEND);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            String lat = String.valueOf(latitude);
            lat += " ";
            bw.write(lat);
            bw.newLine();
            String lon =  String.valueOf(longitude);
            lon += " ";
            bw.write(lon);
            bw.newLine();
            String locationName = name;
            locationName += " ";
            bw.write(locationName);
            bw.newLine();

            bw.close();
        }
        catch(IOException e) {

            e.printStackTrace();
        }
    }//END OF FLUSHLOCATIONTODISK

    // reads the text file and creates one long string of all the latitudes and longitudes
    private String readFromFile() {

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

    // SRP method for getting the address from the string entered in search bar
    // validates address and toasts error messages if weird address is input
    public Address getAddress(String location) {

        List<Address> addressList = null;

        // set null so if gecoder cant make an address so the location entered
        // return null and error check in add and search method
        Address address = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // create new LatLng with latitude and longitude taken from input address
        if (!addressList.isEmpty()) {

            // get address if exists
            address = addressList.get(0);
        }
        // returns address or returns null
        return address;
    }

    // linked to a button which stores the latitude and longitude on the textfile and puts a
    // star marker on the map
    // when the app is closed and reopened these markers are gone but the onmapready reprints
    // all these markers

    // need to figure out how to manage adding the same address
    // markers are all on top of each other so you only see one marker
    // but if you add the same location twice, there's actually two markers
    public void onAdd(View view) {

        final EditText input = new EditText(MapsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Name location")
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //String name = input.getText().toString();
                        //  getName.add(0, input.getText().toString());
                        EditText location_add = (EditText) findViewById(R.id.etAddress);
                        String location = location_add.getText().toString();

                        Address address = getAddress(location);

                        // if address is null print error message, wait for new address
                        if (address == null && sMarkSet == false) {

                            Toast.makeText(MapsActivity.this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
                        }
                        else if (address!=null){

                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();
                            String name = input.getText().toString();
                            LatLng latLng = new LatLng(latitude, longitude);


                            // send the location to txtfile
                            flushLocationsToDisk(latitude, longitude, name);

                            // this marker is the red pin from search
                            // so if you add an address the red pin disappears
                            // only one red pin at a time
                            if (searchMarker != null) {

                                searchMarker.remove();
                            }
                            Marker favoriteMarker = addMarker(mMap, latLng, input.getText().toString(), true);
                            // position camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 5000, null);

                            favoriteMarker.showInfoWindow();
                        }
                        else{
                            double latitude = sMarkerPosition.latitude;
                            double longitude = sMarkerPosition.longitude;
                            String name = input.getText().toString();
                            flushLocationsToDisk(latitude, longitude, name);
                            if(searchMarker!= null) {
                                searchMarker.remove();
                                sMarkSet = false;
                            }
                            Marker favoriteMarker = addMarker(mMap, sMarkerPosition, input.getText().toString(), true);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sMarkerPosition));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 5000, null);
                            favoriteMarker.showInfoWindow();
                        }

/*
                        EditText location_add = (EditText) findViewById(R.id.etAddress);
                        String location = location_add.getText().toString();

                        double latitude = 0;
                        double longitude = 0;
                        LatLng latLng = null;

                        if(searchMarker != null) {

                            latLng = searchMarker.getPosition();
                        }

                        Address address;
                        address = getAddress(location);


                        // if address is null print error message, wait for new address
                        if (address == null || searchMarker == null) {

                            Toast.makeText(MapsActivity.this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
                        }
                        else if(address == null && latLng != null) {

                            latitude = latLng.latitude;
                            longitude = latLng.longitude;
                        }
                        else if(address != null && latLng == null){

                            latitude = address.getLatitude();
                            longitude = address.getLongitude();

                            latLng = new LatLng(latitude, longitude);
                        }

                        String name = input.getText().toString();

                        // send the location to txtfile
                        flushLocationsToDisk(latitude, longitude, name);

                        // this marker is the red pin from search
                        // so if you add an address the red pin disappears
                        // only one red pin at a time
                        if (searchMarker != null) {

                            searchMarker.remove();
                        }
                        Marker favoriteMarker = addMarker(mMap, latLng, name, true);
                        // position camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 5000, null);

                        favoriteMarker.showInfoWindow();
*/
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();


    }

    public void clickMap() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(final LatLng latLng) {

                if (searchMarker != null) {

                    searchMarker.remove();
                    searchMarker = null;
                    sMarkSet = false;
                }

                searchMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                sMarkerPosition = searchMarker.getPosition();
                sMarkSet = true;
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        });
    }
    // linked to search bar and search button for addresses
    // puts a red pin marker and doesn't add address to favorites
    // this is just to separate the map search and the map favorite add
    public void onSearch(View view) {

        EditText location_add = (EditText) findViewById(R.id.etAddress);
        String location = location_add.getText().toString();

        Address address = getAddress(location);

        // if address is null print error message, wait for new address
        if(address == null) {

            Toast.makeText(MapsActivity.this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
        }
        else {

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            // this marker is the red pin from search
            // so if you add an address the red pin disappears
            // only one red pin at a time
            if (searchMarker != null) {

                searchMarker.remove();
                searchMarker = null;
            }
            searchMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            // position camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 5000, null);
        }

    }

    // addMarker method for SRP
    // change method to have a icon parameter for on search display normal marker pin
    // when favorited change to star
    // right now it is star as default ( normal marker pin is to just  remove .icon.... )
    private Marker addMarker(GoogleMap map, LatLng latLng,
                             String title, boolean drag) {
        Marker marker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .draggable(drag)
                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.btn_star_big_on))
        );

        return marker;
    }



   /* public void locationNotification(Location location){
        float[] result = new float[1];
        Location.distanceBetween(location.getLatitude(),location.getLongitude(),32.7157, 117.1611, result);
        Location desTo = new Location(location);
        desTo.setLatitude(32.7157);
        desTo.setLongitude(117.1611);
        System.out.println("Whats lat: "+ location.getLatitude());
        System.out.println("Whats long: "+ location.getLongitude());
        //location.distanceTo(desTo);
        System.out.println("This is distance between to points: " + location.distanceTo(desTo));
        System.out.println("This is the result of the location of the distance between: " + result[0]);
    }
*/
}
