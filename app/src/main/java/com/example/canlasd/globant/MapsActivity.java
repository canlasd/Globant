package com.example.canlasd.globant;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String thirty_days;
    String mGeoJsonUrl;
    private final static String mLogTag = "GeoJsonDemo";
    ArrayList<String> frequency;
    int central, southern, bayview, mission, northern, park, richmond, ingleside, taraval, tenderloin;
    JSONArray array;
    Bitmap oldImage, newImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // timestamp from 30 days ago

        thirty_days = getPreviousStamp();


        // URL which gets data reported from the last month
        mGeoJsonUrl = "https://data.sfgov.org/resource/ritf-b9ki.json?$where=date>'" + thirty_days + "'";
        System.out.println(mGeoJsonUrl);

        // create new arraylist

        frequency = new ArrayList<String>();


        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        // Download the json file
        downloadGeoJsonFile.execute(mGeoJsonUrl);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // set initial position on map
        LatLng san_francisco = new LatLng(37.76, -122.44);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(san_francisco, 12));


    }


    public static String getPreviousStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");//2015-01-10T12:00:00
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -30);
        String thirty = dateFormat.format(now.getTime());
        return thirty;
    }

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {


            try {
                URL url = new URL(mGeoJsonUrl);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestProperty("X-App-Token", "t1AxVkHpwxL2OznzjtRpFbQEA");
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();


                array = new JSONArray(result.toString());


                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String pddistrict = obj.optString("pddistrict").toString();
                    frequency.add(pddistrict);

                    System.out.println(pddistrict);


                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(mLogTag, "GeoJSON file could not be read");

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            //get crime frequency for each district
            central = Collections.frequency(frequency, "CENTRAL");
            southern = Collections.frequency(frequency, "SOUTHERN");
            bayview = Collections.frequency(frequency, "BAYVIEW");
            mission = Collections.frequency(frequency, "MISSION");
            northern = Collections.frequency(frequency, "NORTHERN");
            park = Collections.frequency(frequency, "PARK");
            richmond = Collections.frequency(frequency, "RICHMOND");
            ingleside = Collections.frequency(frequency, "INGLESIDE");
            taraval = Collections.frequency(frequency, "TARAVAL");
            tenderloin = Collections.frequency(frequency, "TENDERLOIN");

            // Add markers to police stations
            LatLng central_pos = new LatLng(37.7991252, -122.4121217);
            mMap.addMarker(new MarkerOptions().position(central_pos).title("Central Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(central))));


            LatLng southern_pos = new LatLng(37.77238, -122.389412);
            mMap.addMarker(new MarkerOptions().position(southern_pos).title("Southern Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(southern))));

            LatLng bayview_pos = new LatLng(37.729751, -122.397903);
            mMap.addMarker(new MarkerOptions().position(bayview_pos).title("Bayview Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(bayview))));

            LatLng mission_pos = new LatLng(37.762849, -122.4241937);
            mMap.addMarker(new MarkerOptions().position(mission_pos).title("Mission Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(mission))));

            LatLng northern_pos = new LatLng(37.7801858, -122.4346552);
            mMap.addMarker(new MarkerOptions().position(northern_pos).title("Northern Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(northern))));

            LatLng park_pos = new LatLng(37.767797, -122.4574757);
            mMap.addMarker(new MarkerOptions().position(park_pos).title("Park Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(park))));

            LatLng richmond_pos = new LatLng(37.7799276, -122.4666555);
            mMap.addMarker(new MarkerOptions().position(richmond_pos).title("Richmond Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(richmond))));

            LatLng ingleside_pos = new LatLng(37.7246756, -122.4484041);
            mMap.addMarker(new MarkerOptions().position(ingleside_pos).title("Ingleside Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(ingleside))));

            LatLng taraval_pos = new LatLng(37.7437335, -122.483689);
            mMap.addMarker(new MarkerOptions().position(taraval_pos).title("Taraval Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(taraval))));

            LatLng tenderloin_pos = new LatLng(37.7836739, -122.4150878);
            mMap.addMarker(new MarkerOptions().position(tenderloin_pos).title("Tenderloin Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(tenderloin))));

        }


    }

    // change color of marker based on crime frequency.  Hex values converted to hue
    private static float frequencyToColor(int frequency) {
        if (frequency >= 0 && frequency <= 10) {
            return 0f;
        } else if (frequency >= 11 && frequency <= 25) {
            return 14f;
        } else if (frequency >= 26 && frequency <= 50) {
            return 19f;

        } else if (frequency >= 51 && frequency <= 100) {
            return 30f;

        } else if (frequency >= 101 && frequency <= 150) {
            return 36f;
        } else if (frequency >= 151 && frequency <= 200) {
            return 50f;
        } else if (frequency >= 151 && frequency <= 250) {
            return 65f;
        } else {
            return 81f;
        }
    }

/*
The marker colors should contrast between {#ff0000 (0),
 #eb3600 (14), #e54800(19), #d86d00(30), #d27f00(36), #c5a300 (50),
  #b9c800 (65), #a6ff00 (81)} where FF0000 to #b9c800 are the most
  reported areas, and the remaining three are #af6ff00.
 */


}
