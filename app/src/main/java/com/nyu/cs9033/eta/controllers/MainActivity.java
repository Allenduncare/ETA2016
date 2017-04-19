package com.nyu.cs9033.eta.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.GPSUpdater;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripDatabaseHelper;
import com.nyu.cs9033.eta.models.WebTalker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static Trip trip = null;
    String displayText = "";
    private static String url = "http://cs9033-homework.appspot.com/";
    int REQUEST_CODE = 1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TripDatabaseHelper helper = new TripDatabaseHelper(this);
        setContentView(R.layout.activity_main);
        Button button1 = (Button)findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCreateTripActivity();
            }
        });
        final Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startViewTripActivity();
            }
        });
        if(trip == null)
        {
            TextView view = (TextView)findViewById(R.id.ActiveTripDetails);
            view.setText("No active trips!");

        }
    }

    /**
     * This method should start the
     * Activity responsible for creating
     * a Trip.
     * Needs to grab intent result to retrieve trip details.
     */
    public void startCreateTripActivity() {
        Intent i = new Intent(this, CreateTripActivity.class);
        startActivity(i);

    }

    /**
     * This method should start the
     * Activity responsible for viewing
     * a Trip.
     */
    //Doesn't need result as it only displays info
    public void startViewTripActivity() {
        Intent i = new Intent(this,TripHistoryActivity.class);
        startActivityForResult(i,REQUEST_CODE);
    }


    /**
     * Receive result from CreateTripActivity here.
     * Can be used to save instance of Trip object
     * which can be viewed in the ViewTripActivity.
     * <p>
     * Note: This method will be called when a Trip
     * object is returned to the main activity.
     * Remember that the Trip will not be returned as
     * a Trip object; it will be in the persisted
     * Parcelable form. The actual Trip object should
     * be created and saved in a variable for future
     * use, i.e. to view the trip.
*/
   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode==REQUEST_CODE && resultCode == RESULT_OK) {
            trip = (Trip) data.getParcelableExtra("Trip");


                new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(600);
                                if (trip != null) {
                                    long trip_id = trip.getTripID();
                                    String[] params = {"TRIP_STATUS", String.valueOf(trip_id)};
                                    ServerRequest wt = new ServerRequest();
                                    wt.execute(params);}
                                Intent i = new Intent(getApplicationContext(), GPSUpdater.class);
                                startService(i);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }


    class ServerRequest extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            String status = "Failure";
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null & netInfo.isConnected()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("command", args[0]);
                    jsonObject.put("trip_id", args[1]);
                } catch (Exception e) {
                    Log.e("Error!", e.toString());
                    e.printStackTrace();
                }
                JSONObject json = WebTalker.makeHttpRequest(url, "POST", jsonObject);

                Log.d("JSON",
                        "JSON Response in CreateTripActivity :"
                                + json.toString());

                try {
                    JSONArray distanceleft = json.getJSONArray("distance_left");
                    JSONArray timeleft = json.getJSONArray("time_left");
                    JSONArray people = json.getJSONArray("people");
                    String destination = trip.getDestination();
                    displayText = "Destination: " + destination + "\n";
                    for (int i = 0; i < distanceleft.length(); i++) {
                        long longVal = (Long)timeleft.get(i);
                        int hours = (int) longVal / 3600;
                        int remainder = (int) longVal - hours * 3600;
                        int mins = remainder / 60;
                        remainder = remainder - mins * 60;
                        int secs = remainder;
                        displayText += people.get(i) + " is " + distanceleft.get(i) + " with an ETA of " + hours + " hours and "+mins+" minutes" + ".\n";
                    }
                } catch (JSONException je) {
                    Log.e("ERROR", "Error in CreateNewTrip.doInBackground()"
                            + je.toString());
                }
                status = displayText;
            } else {
                Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_LONG);
                status = "Failed to get trip details from server";
            }
            return status;
        }

        protected void onPostExecute(String text) {
            TextView view = (TextView) findViewById(R.id.ActiveTripDetails);
            view.setText(text);

        }
    }}
