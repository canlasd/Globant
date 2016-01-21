package com.example.canlasd.globant;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// used AppCompatActivity to show action bar
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String thirty_days;
    String mGeoJsonUrl, mGeoJsonUrlnext;
    private final static String mLogTag = "GeoJsonDemo";
    ArrayList<String> frequency;
    JSONArray array;
    Map<String, Integer> frequency_unsorted = new HashMap<>();
    Map<String, Integer> frequency_sorted = new HashMap<>();
    boolean keepGoing = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // timestamp from 30 days ago
        thirty_days = getPreviousStamp();


        // URL which gets data reported from the last month

        mGeoJsonUrl = "https://data.sfgov.org/resource/ritf-b9ki.json?$where=date>'" + thirty_days + "'&$limit=5000&$offset=0";

        // URL to get data for the next batch (if any)
        mGeoJsonUrlnext = "https://data.sfgov.org/resource/ritf-b9ki.json?$where=date>'" + thirty_days + "'&$limit=5000&$offset=5000";
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

                URL url = new URL(params[0]);
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

                if (array.length() == 0) {
                    keepGoing = false;

                }


                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String pddistrict = obj.optString("pddistrict");
                    // add elements to the arraylist
                    frequency.add(pddistrict);


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

            //get crime frequency for each district and place in map
            frequency_unsorted.put("central", (Collections.frequency(frequency, "CENTRAL")));
            frequency_unsorted.put("southern", (Collections.frequency(frequency, "SOUTHERN")));
            frequency_unsorted.put("bayview", (Collections.frequency(frequency, "BAYVIEW")));
            frequency_unsorted.put("mission", (Collections.frequency(frequency, "MISSION")));
            frequency_unsorted.put("northern", (Collections.frequency(frequency, "NORTHERN")));
            frequency_unsorted.put("park", (Collections.frequency(frequency, "PARK")));
            frequency_unsorted.put("richmond", (Collections.frequency(frequency, "RICHMOND")));
            frequency_unsorted.put("ingleside", (Collections.frequency(frequency, "INGLESIDE")));
            frequency_unsorted.put("taraval", (Collections.frequency(frequency, "TARAVAL")));
            frequency_unsorted.put("tenderloin", (Collections.frequency(frequency, "TENDERLOIN")));


            // sort map by value
            frequency_sorted = sortByComparator(frequency_unsorted);

            System.out.println("Unsort Map......");
            printMap(frequency_unsorted);

            System.out.println("\nSorted Map......");
            printMap(frequency_sorted);

            List<String> indexes = new ArrayList<>(frequency_sorted.keySet());

            // Add markers to police stations
            LatLng central_pos = new LatLng(37.7991252, -122.4121217);
            mMap.addMarker(new MarkerOptions().position(central_pos).title("Central Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("central")))));


            LatLng southern_pos = new LatLng(37.77238, -122.389412);
            mMap.addMarker(new MarkerOptions().position(southern_pos).title("Southern Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("southern")))));

            LatLng bayview_pos = new LatLng(37.729751, -122.397903);
            mMap.addMarker(new MarkerOptions().position(bayview_pos).title("Bayview Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("bayview")))));

            LatLng mission_pos = new LatLng(37.762849, -122.4241937);
            mMap.addMarker(new MarkerOptions().position(mission_pos).title("Mission Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("mission")))));

            LatLng northern_pos = new LatLng(37.7801858, -122.4346552);
            mMap.addMarker(new MarkerOptions().position(northern_pos).title("Northern Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("northern")))));

            LatLng park_pos = new LatLng(37.767797, -122.4574757);
            mMap.addMarker(new MarkerOptions().position(park_pos).title("Park Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("park")))));

            LatLng richmond_pos = new LatLng(37.7799276, -122.4666555);
            mMap.addMarker(new MarkerOptions().position(richmond_pos).title("Richmond Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("richmond")))));

            LatLng ingleside_pos = new LatLng(37.7246756, -122.4484041);
            mMap.addMarker(new MarkerOptions().position(ingleside_pos).title("Ingleside Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("ingleside")))));

            LatLng taraval_pos = new LatLng(37.7437335, -122.483689);
            mMap.addMarker(new MarkerOptions().position(taraval_pos).title("Taraval Station")
                    .icon(BitmapDescriptorFactory.defaultMarker(frequencyToColor(indexes.indexOf("taraval")))));

            LatLng tenderloin_pos = new LatLng(37.7836739, -122.4150878);
            mMap.addMarker(new MarkerOptions().position(tenderloin_pos).title("Tenderloin Station")
                    .icon(BitmapDescriptorFactory.defaultMarker((frequencyToColor(indexes.indexOf("tenderloin"))))));

            if (keepGoing==true){
                showDialog();
            }

            else{
                showFinishDialog();
            }

        }


    }

    // change color of marker based on crime frequency.  Hex values converted to hue
    private static float frequencyToColor(int rank) {
        if (rank == 0) {
            return 81f;
        } else if (rank == 1) {
            return 81f;
        } else if (rank == 2) {
            return 81f;
        } else if (rank == 3) {
            return 64.5f;
        } else if (rank == 4) {
            return 50f;
        } else if (rank == 5) {
            return 36.3f;
        } else if (rank == 6) {
            return 30f;
        } else if (rank == 7) {
            return 19f;
        } else if (rank == 8) {
            return 14f;
        } else if (rank == 9) {
            return 0f;
        } else {
            return 81f;
        }
    }


    private static Map<String, Integer> sortByComparator(Map<String, Integer> frequency_unsorted) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(frequency_unsorted.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("[Key] : " + entry.getKey()
                    + " [Value] : " + entry.getValue());
        }
    }

    private void showDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        // set title
        alertDialogBuilder.setTitle("Do you want to add more data?");
        alertDialogBuilder.setCancelable(true);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            DownloadGeoJsonFile downloadGeoJsonFile_next = new DownloadGeoJsonFile();
                            // Download the json file
                            downloadGeoJsonFile_next.execute(mGeoJsonUrlnext);
                        } catch (Exception e) {
                            //Exception
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do something if you need
                        dialog.cancel();
                        keepGoing=false;
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();


    }

    private void showFinishDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        // set title
        alertDialogBuilder.setTitle("All Available Data has been Added");
        alertDialogBuilder.setCancelable(true);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do something if you need
                        dialog.cancel();
                        keepGoing=false;
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();


        // show it
        alertDialog.show();


    }




}
