package com.example.canlasd.globant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

// used AppCompatActivity to show action bar
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static GoogleMap google_map;
    String thirty_days;
    String initial_url, next_url;
    public static final String URL = "https://data.sfgov.org/resource/ritf-b9ki.json?";
    private final static String map_log = "map_log";

    Integer increment = 2000;
    Integer count = 0;


    static ArrayList<String> frequency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // timestamp from 30 days ago
        thirty_days = getPreviousStamp();


        // URL which gets data reported from the last month
        initial_url = URL + "$where=date>'" + thirty_days + "'&$limit=2000&$offset=0";


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // create new arraylist
        frequency = new ArrayList<String>();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;
        // set initial position on map
        LatLng san_francisco = new LatLng(37.76, -122.44);
        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(san_francisco, 12));

        DownloadData download = new DownloadData();
        download.startDownload(initial_url);
        checkStatus();


    }


    public String getPreviousStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");//2015-01-10T12:00:00
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -30);
        String thirty = dateFormat.format(now.getTime());
        return thirty;
    }

    public void showDialog() {

        if (count > 0) {
            increment = increment + 2000;
        }

        count = count + 1;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle("Do you want to add more data?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            // URL to get data for the next batch (if any)
                            next_url = URL + "$where=date>'" + thirty_days + "'&$limit=2000&$offset=" + increment;

                            DownloadData download_next = new DownloadData();
                            // Download the json file
                            download_next.startDownload(next_url);
                            checkStatus();


                        } catch (Exception e) {

                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();


    }

    public void showFinishDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setTitle("All Available Data has been Added");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();


    }

    public void checkStatus() {
        if (DownloadData.status ==1) {

            showFinishDialog();

        } else {
            showDialog();
        }
    }


}






