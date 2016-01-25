package com.example.canlasd.globant;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


class DownloadData {

    private final static String log_tag = "log_download_data";
    static int status;
    private final Map<String, Integer> frequency_unsorted = new HashMap<>();
    public class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        // get hue values from color strings
        final float hue_a6ff00 = convertStringToHue("#a6ff00");
        final float hue_eb3600 = convertStringToHue("#eb3600");
        final float hue_e54800 = convertStringToHue("#e54800");
        final float hue_d86d00 = convertStringToHue("#d86d00");
        final float hue_d27f00 = convertStringToHue("#d27f00");
        final float hue_c5a300 = convertStringToHue("#c5a300");
        final float hue_b9c800 = convertStringToHue("#b9c800");
        final float hue_ff0000 = convertStringToHue("#ff0000");


        @Override
        protected JSONObject doInBackground(String... params) {


            try {


                Log.d(log_tag, params[0]);
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

                Log.d(log_tag, "Actual String: " + result.toString());


                reader.close();
                stream.close();


                JSONArray array = new JSONArray(result.toString());

                Log.d(log_tag, "Size of Array: " + array.length());
                // check if array is empty
                if (array.isNull(0)) {
                    status++;
                    Log.d(log_tag, "Status number: " + status);
                }


                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    String pddistrict = obj.optString("pddistrict");
                    // add elements to the arraylist
                    MapsActivity.frequency.add(pddistrict);


                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(log_tag, "GeoJSON file could not be read");


            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {


            //get crime frequency for each district and place in map
            frequency_unsorted.put("central",
                    (Collections.frequency(MapsActivity.frequency, "CENTRAL")));
            frequency_unsorted.put("southern",
                    (Collections.frequency(MapsActivity.frequency, "SOUTHERN")));
            frequency_unsorted.put("bayview",
                    (Collections.frequency(MapsActivity.frequency, "BAYVIEW")));
            frequency_unsorted.put("mission",
                    (Collections.frequency(MapsActivity.frequency, "MISSION")));
            frequency_unsorted.put("northern",
                    (Collections.frequency(MapsActivity.frequency, "NORTHERN")));
            frequency_unsorted.put("park",
                    (Collections.frequency(MapsActivity.frequency, "PARK")));
            frequency_unsorted.put("richmond",
                    (Collections.frequency(MapsActivity.frequency, "RICHMOND")));
            frequency_unsorted.put("ingleside",
                    (Collections.frequency(MapsActivity.frequency, "INGLESIDE")));
            frequency_unsorted.put("taraval",
                    (Collections.frequency(MapsActivity.frequency, "TARAVAL")));
            frequency_unsorted.put("tenderloin",
                    (Collections.frequency(MapsActivity.frequency, "TENDERLOIN")));


            // sort map by value
            Map<String, Integer> frequency_sorted = sortByComparator(frequency_unsorted);

            Log.d(log_tag, "Unsorted Map......");
            printMap(frequency_unsorted);

            Log.d(log_tag, "Sorted Map......");
            printMap(frequency_sorted);

            List<String> indexes = new ArrayList<>(frequency_sorted.keySet());

            // Add markers to police stations


            LatLng central_pos = new LatLng(37.7991252, -122.4121217);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(central_pos)
                    .title("Central Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("central")))));


            LatLng southern_pos = new LatLng(37.77238, -122.389412);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(southern_pos)
                    .title("Southern Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("southern")))));

            LatLng bayview_pos = new LatLng(37.729751, -122.397903);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(bayview_pos)
                    .title("Bayview Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("bayview")))));

            LatLng mission_pos = new LatLng(37.762849, -122.4241937);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(mission_pos)
                    .title("Mission Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("mission")))));

            LatLng northern_pos = new LatLng(37.7801858, -122.4346552);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(northern_pos)
                    .title("Northern Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("northern")))));

            LatLng park_pos = new LatLng(37.767797, -122.4574757);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(park_pos)
                    .title("Park Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("park")))));

            LatLng richmond_pos = new LatLng(37.7799276, -122.4666555);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(richmond_pos)
                    .title("Richmond Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("richmond")))));

            LatLng ingleside_pos = new LatLng(37.7246756, -122.4484041);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(ingleside_pos)
                    .title("Ingleside Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("ingleside")))));

            LatLng taraval_pos = new LatLng(37.7437335, -122.483689);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(taraval_pos)
                    .title("Taraval Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(frequencyToColor(indexes.indexOf("taraval")))));

            LatLng tenderloin_pos = new LatLng(37.7836739, -122.4150878);
            MapsActivity.google_map.addMarker(new MarkerOptions()
                    .position(tenderloin_pos)
                    .title("Tenderloin Station")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker((frequencyToColor(indexes.indexOf("tenderloin"))))));


        }


        // change color of marker based on crime frequency.  Hex values converted to hue
        private float frequencyToColor(int rank) {
            if (rank == 0) {
                return hue_a6ff00;
            } else if (rank == 1) {
                return hue_a6ff00;
            } else if (rank == 2) {
                return hue_a6ff00;
            } else if (rank == 3) {
                return hue_b9c800;
            } else if (rank == 4) {
                return hue_c5a300;
            } else if (rank == 5) {
                return hue_d27f00;
            } else if (rank == 6) {
                return hue_d86d00;
            } else if (rank == 7) {
                return hue_e54800;
            } else if (rank == 8) {
                return hue_eb3600;
            } else if (rank == 9) {
                return hue_ff0000;
            } else {
                return hue_a6ff00;
            }
        }


        private Map<String, Integer> sortByComparator(Map<String, Integer> frequency_unsorted) {

            // Convert Map to List
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<>(frequency_unsorted.entrySet());

            // Sort list with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });

            // Convert sorted map back to a Map
            Map<String, Integer> sorted_map = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sorted_map.put(entry.getKey(), entry.getValue());
            }
            return sorted_map;
        }

        public void printMap(Map<String, Integer> map) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Log.d(log_tag, "[Key] : " + entry.getKey()
                        + " [Value] : " + entry.getValue());
            }
        }


    }

    public void startDownload(String link) {


        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        // Download the json file
        downloadGeoJsonFile.execute(link);

        // finish downloading before showing alert dialog
        try {
            downloadGeoJsonFile.get(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }


    }

    private float convertStringToHue(String value) {

        int color_new = Color.parseColor(value);
        int r = Color.red(color_new);
        int g = Color.green(color_new);
        int b = Color.blue(color_new);


        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);

        float hue = hsv[0];
        Log.d(log_tag, "Hue Value of" + value + " is: " + hue);

        return hue;
    }


}




