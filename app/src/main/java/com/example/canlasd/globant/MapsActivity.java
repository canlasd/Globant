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
    private String thirty_days;
    private String initial_url;
    private String next_url;
    private static final String URL = "https://data.sfgov.org/resource/ritf-b9ki.json?";
    private Integer increment = 1000;
    private Integer count = 0;
    static ArrayList<String> frequency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // timestamp from 30 days ago
        thirty_days = getPreviousStamp();


        // URL which gets data reported from the last month
        initial_url = URL + "$where=date>'" + thirty_days + "'&$limit=1000&$offset=0";


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map_fragment.getMapAsync(this);


        // create new arraylist
        frequency = new ArrayList<>();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;
        // set initial position on map
        LatLng san_francisco = new LatLng(37.76, -122.44);
        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(san_francisco, 12));

        // download data from url
        DownloadData download_initial = new DownloadData();
        download_initial.startDownload(initial_url);
        // check if we need to continue
        checkStatus();


    }


    private String getPreviousStamp() {
        DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -30);
        return date_format.format(now.getTime());
    }

    private void showDialog() {

        if (count > 0) {
            increment = increment + 1000;
        }

        count = count + 1;

        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        alert_dialog.setTitle(R.string.alert_dialog);
        alert_dialog.setCancelable(true);
        alert_dialog
                .setCancelable(true)
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            // URL to get data for the next batch (if any)
                            next_url = URL + "$where=date>'" + thirty_days +
                                    "'&$limit=1000&$offset=" + increment;


                            DownloadData download_next = new DownloadData();
                            download_next.startDownload(next_url);


                            checkStatus();


                        } catch (Exception ignored) {

                        }
                    }
                })
                .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog continue_dialog = alert_dialog.create();


        continue_dialog.show();


    }

    private void showFinishDialog() {

        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(
                MapsActivity.this, R.style.MyAlertDialogStyle);
        alert_dialog.setTitle(R.string.finish_dialog);
        alert_dialog.setCancelable(true);
        alert_dialog
                .setCancelable(true)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog finish_dialog = alert_dialog.create();


        finish_dialog.show();


    }

    private void checkStatus() {
        if (DownloadData.status > 0) {

            showFinishDialog();

        } else {
            showDialog();
        }

    }


    public void onResume() {
        super.onResume();
        DownloadData.status = 0;


    }


}















